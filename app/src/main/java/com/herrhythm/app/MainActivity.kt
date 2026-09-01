package com.herrhythm.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.*
import com.herrhythm.app.ui.screens.*
import com.herrhythm.app.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private lateinit var storageManager: LocalStorageManager
    private lateinit var sensorRepository: HealthSensorRepository
    private lateinit var memoryRepository: UserMemoryRepository
    private lateinit var cycleRepository: CycleRepository
    private lateinit var fitnessRepository: FitnessRepository
    private lateinit var doctorRepository: DoctorRepository
    private lateinit var telegramAlertManager: TelegramAlertManager
    private lateinit var nyraEngine: NyraAgentEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageManager = LocalStorageManager(applicationContext)
        sensorRepository = MockWatchDataProvider()
        memoryRepository = UserMemoryRepository()
        telegramAlertManager = TelegramAlertManager(applicationContext)
        
        // Restore memories from storage if available
        storageManager.getMemories()?.let { storedMemories ->
            if (storedMemories.isNotEmpty()) {
                storedMemories.forEach { memoryRepository.addMemory(it.category, it.content) }
            }
        }

        cycleRepository = CycleRepository()
        // Restore cycle settings from storage
        val savedCycle = storageManager.getCycleSettings()
        cycleRepository.updateCycleSettings(
            startDate = savedCycle.lastPeriodStartDate,
            cycleLength = savedCycle.cycleLengthDays,
            periodLength = savedCycle.periodDurationDays
        )

        // Restore daily logs
        storageManager.getAllDailyLogs().forEach { log ->
            cycleRepository.saveDailyLog(log)
        }

        fitnessRepository = FitnessRepository()
        doctorRepository = DoctorRepository(storageManager)

        nyraEngine = NyraAgentEngine(
            memoryRepository = memoryRepository,
            cycleRepository = cycleRepository,
            healthRepository = sensorRepository,
            telegramAlertManager = telegramAlertManager
        )

        // Load profile and safety settings into engine
        storageManager.getUserProfile()?.let { profile ->
            nyraEngine.setUserProfile(profile)
        }
        val safetySettings = storageManager.getSafetySettings()
        nyraEngine.setSafetySettings(safetySettings)

        setContent {
            HerRhythmTheme {
                MainAppContainer(
                    storageManager = storageManager,
                    sensorRepository = sensorRepository,
                    memoryRepository = memoryRepository,
                    cycleRepository = cycleRepository,
                    fitnessRepository = fitnessRepository,
                    doctorRepository = doctorRepository,
                    telegramAlertManager = telegramAlertManager,
                    nyraEngine = nyraEngine
                )
            }
        }
    }
}

@Composable
fun MainAppContainer(
    storageManager: LocalStorageManager,
    sensorRepository: HealthSensorRepository,
    memoryRepository: UserMemoryRepository,
    cycleRepository: CycleRepository,
    fitnessRepository: FitnessRepository,
    doctorRepository: DoctorRepository,
    telegramAlertManager: TelegramAlertManager,
    nyraEngine: NyraAgentEngine
) {
    var isOnboardingCompleted by remember {
        mutableStateOf(storageManager.isOnboardingCompleted())
    }

    var userProfile by remember {
        mutableStateOf(
            storageManager.getUserProfile() ?: UserProfile(
                name = "Beautiful",
                dateOfBirth = "14/05/2000",
                weightKg = 55.0f,
                hasWatch = true,
                fitnessGoal = "Track my period"
            )
        )
    }

    var safetySettings by remember {
        mutableStateOf(storageManager.getSafetySettings())
    }

    var selectedTab by remember { mutableStateOf("Today") } // Today, Programs, Care, Analysis, NYRA, Health, Calendar, Profile
    var activeRunningSession by remember { mutableStateOf<WorkoutSession?>(null) }
    var showLogPeriodDialog by remember { mutableStateOf(false) }
    var showGynaecologistScreen by remember { mutableStateOf(false) }
    var showFakeCallScreen by remember { mutableStateOf(false) }
    var showQuickActionSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Wire up Fake Call callback in NYRA Engine
    LaunchedEffect(Unit) {
        nyraEngine.setOnTriggerFakeCall {
            showFakeCallScreen = true
        }
    }

    val liveSnapshot by sensorRepository.liveSnapshot.collectAsState()
    val memories by memoryRepository.memories.collectAsState()
    val nyraMessages by nyraEngine.messages.collectAsState()
    val isNyraLoading by nyraEngine.isLoading.collectAsState()
    val activeReminders by nyraEngine.activeReminders.collectAsState()

    // 1. Full Screen Fake Call Screen Overlay
    if (showFakeCallScreen) {
        FakeCallScreen(
            callerName = safetySettings.fakeCallerName,
            callerNumber = safetySettings.fakeCallerNumber,
            onDismiss = { showFakeCallScreen = false }
        )
        return
    }

    // 2. Full screen Gynaecologist Directory overlay
    if (showGynaecologistScreen) {
        GynaecologistScreen(
            doctorRepository = doctorRepository,
            onBack = { showGynaecologistScreen = false }
        )
        return
    }

    // 3. Full screen interactive workout session overlay
    if (activeRunningSession != null) {
        val session = activeRunningSession!!
        WorkoutSessionScreen(
            session = session,
            onClose = { activeRunningSession = null },
            onSessionCompleted = { _ ->
                fitnessRepository.markSessionComplete(
                    programId = "weight_loss",
                    sessionId = session.id,
                    caloriesBurned = session.estimatedCalories
                )
                nyraEngine.sendMessage("I completed the ${session.title} session! Burned ${session.estimatedCalories} kcal.")
                activeRunningSession = null
            }
        )
        return
    }

    // 4. Period log dialog overlay
    if (showLogPeriodDialog) {
        LogPeriodDialog(
            initialDate = LocalDate.now(),
            onDismiss = { showLogPeriodDialog = false },
            onSaveLog = { log, isPeriodStart ->
                cycleRepository.saveDailyLog(log)
                storageManager.saveDailyLog(log)

                if (isPeriodStart) {
                    cycleRepository.updateCycleSettings(
                        startDate = log.date,
                        cycleLength = userProfile.cycleLengthDays,
                        periodLength = userProfile.periodDurationDays
                    )
                    storageManager.saveCycleSettings(
                        startDate = log.date,
                        cycleLength = userProfile.cycleLengthDays,
                        periodLength = userProfile.periodDurationDays
                    )
                }

                val symptomDesc = log.symptoms.joinToString(", ") { it.label }
                val flowDesc = log.flow?.label ?: "Logged"
                nyraEngine.sendMessage("I logged my period/symptoms for ${log.date}: Flow: $flowDesc, Symptoms: $symptomDesc. Note: ${log.notes}")
                showLogPeriodDialog = false
            }
        )
    }

    // 5. Floating Center '+' Quick Action Menu Dialog
    if (showQuickActionSheet) {
        AlertDialog(
            onDismissRequest = { showQuickActionSheet = false },
            containerColor = PookieCardBg,
            title = {
                Text(
                    text = "Quick Actions 🌸",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Log Period
                    Button(
                        onClick = {
                            showQuickActionSheet = false
                            showLogPeriodDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🩸 Log Period / Symptoms", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Safe Exit Fake Call
                    Button(
                        onClick = {
                            showQuickActionSheet = false
                            showFakeCallScreen = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📞 Fake Call (${safetySettings.fakeCallerName})", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Emergency SOS
                    Button(
                        onClick = {
                            showQuickActionSheet = false
                            coroutineScope.launch {
                                val result = telegramAlertManager.sendSosAlert(
                                    userProfile = userProfile,
                                    safetySettings = safetySettings,
                                    reason = "Emergency SOS sent via Quick Actions",
                                    healthSnapshot = liveSnapshot
                                )
                                Toast.makeText(context, result.responseMessage, Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🚨 Emergency SOS Broadcast", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Consult Gynaecologist
                    Button(
                        onClick = {
                            showQuickActionSheet = false
                            showGynaecologistScreen = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PookieLavender),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("👩‍⚕️ Consult Gynaecologists", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Calendar
                    Button(
                        onClick = {
                            showQuickActionSheet = false
                            selectedTab = "Calendar"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PookieCardLight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📅 Open Cycle Calendar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQuickActionSheet = false }) {
                    Text("Close", color = PookieTextMuted)
                }
            }
        )
    }

    // Onboarding flow (only shown once, saved to phone memory)
    if (!isOnboardingCompleted) {
        OnboardingScreen(
            onOnboardingFinished = { profile ->
                userProfile = profile
                storageManager.saveUserProfile(profile)
                storageManager.setOnboardingCompleted(true)
                nyraEngine.setUserProfile(profile)

                val startDate = try {
                    val parts = profile.lastPeriodStart.split("/")
                    if (parts.size == 3) {
                        LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                    } else LocalDate.now().minusDays(10)
                } catch (e: Exception) {
                    LocalDate.now().minusDays(10)
                }

                cycleRepository.updateCycleSettings(
                    startDate = startDate,
                    cycleLength = profile.cycleLengthDays,
                    periodLength = profile.periodDurationDays
                )
                storageManager.saveCycleSettings(
                    startDate = startDate,
                    cycleLength = profile.cycleLengthDays,
                    periodLength = profile.periodDurationDays
                )

                isOnboardingCompleted = true
            }
        )
    } else {
        Scaffold(
            containerColor = PookieDarkBg,
            bottomBar = {
                // POOKIE CAT REFERENCE BOTTOM NAVIGATION BAR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PookieNavBg)
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Today
                        val isToday = selectedTab == "Today"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedTab = "Today" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Brightness5,
                                contentDescription = "Today",
                                tint = if (isToday) PookiePinkPrimary else PookieTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Today", fontSize = 10.sp, color = if (isToday) PookiePinkPrimary else PookieTextMuted, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
                        }

                        // 2. Programs / Fitness
                        val isPrograms = selectedTab == "Programs"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedTab = "Programs" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = "Programs",
                                tint = if (isPrograms) PookiePinkPrimary else PookieTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Programs", fontSize = 10.sp, color = if (isPrograms) PookiePinkPrimary else PookieTextMuted, fontWeight = if (isPrograms) FontWeight.Bold else FontWeight.Normal)
                        }

                        // 3. CENTER BIG FLOATING '+' BUTTON
                        Box(
                            modifier = Modifier
                                .offset(y = (-10).dp)
                                .size(52.dp)
                                .shadow(10.dp, CircleShape, spotColor = PookiePinkPrimary)
                                .clip(CircleShape)
                                .background(PookiePinkPrimary)
                                .clickable { showQuickActionSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(28.dp))
                        }

                        // 4. Care / Gynaecologist
                        val isCare = selectedTab == "Care"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedTab = "Care" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Care",
                                tint = if (isCare) PookiePinkPrimary else PookieTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Care 👩‍⚕️", fontSize = 10.sp, color = if (isCare) PookiePinkPrimary else PookieTextMuted, fontWeight = if (isCare) FontWeight.Bold else FontWeight.Normal)
                        }

                        // 5. Analysis
                        val isAnalysis = selectedTab == "Analysis"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedTab = "Analysis" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isAnalysis) PookiePinkCard else Color.Transparent)
                                    .padding(horizontal = 10.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.BarChart,
                                    contentDescription = "Analysis",
                                    tint = if (isAnalysis) PookiePinkText else PookieTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Analysis", fontSize = 10.sp, color = if (isAnalysis) PookiePinkPrimary else PookieTextMuted, fontWeight = if (isAnalysis) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    "Today" -> HomeScreen(
                        userProfile = userProfile,
                        cycleInfo = cycleRepository.getCycleInfo(),
                        healthSnapshot = liveSnapshot,
                        reminders = activeReminders,
                        onOpenNyraChat = { selectedTab = "NYRA" },
                        onOpenHealthDetail = { selectedTab = "Health" },
                        onOpenFitness = { selectedTab = "Programs" },
                        onOpenWatchManager = { selectedTab = "Profile" },
                        onOpenGynaecologists = { showGynaecologistScreen = true },
                        onOpenLogPeriodDialog = { showLogPeriodDialog = true },
                        onTriggerFakeCall = { showFakeCallScreen = true },
                        onTriggerSos = {
                            coroutineScope.launch {
                                val result = telegramAlertManager.sendSosAlert(
                                    userProfile = userProfile,
                                    safetySettings = safetySettings,
                                    reason = "One-Touch Emergency SOS from Home Screen",
                                    healthSnapshot = liveSnapshot
                                )
                                Toast.makeText(context, result.responseMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                    "Programs" -> FitnessScreen(
                        snapshot = liveSnapshot,
                        fitnessRepository = fitnessRepository,
                        onStartWorkout = { session -> activeRunningSession = session },
                        onGenerateWorkout = {
                            nyraEngine.sendMessage("Generate a custom 30 min workout plan for my current energy level")
                            selectedTab = "NYRA"
                        }
                    )
                    "Care" -> GynaecologistScreen(
                        doctorRepository = doctorRepository,
                        onBack = { selectedTab = "Today" }
                    )
                    "Calendar" -> CalendarScreen(
                        cycleInfo = cycleRepository.getCycleInfo(),
                        onBackClick = { selectedTab = "Today" }
                    )
                    "Health" -> HealthScreen(
                        snapshot = liveSnapshot
                    )
                    "Analysis" -> AnalysisScreen(
                        userProfile = userProfile,
                        cycleInfo = cycleRepository.getCycleInfo(),
                        healthSnapshot = liveSnapshot,
                        onOpenSettings = { selectedTab = "Profile" },
                        onOpenLogPeriod = { showLogPeriodDialog = true },
                        onTogglePregnancy = { enabled ->
                            val updated = userProfile.copy(isPregnancyModeEnabled = enabled)
                            userProfile = updated
                            storageManager.saveUserProfile(updated)
                        },
                        onUpdateWeight = { newWeight ->
                            val updated = userProfile.copy(weightKg = newWeight)
                            userProfile = updated
                            storageManager.saveUserProfile(updated)
                        }
                    )
                    "NYRA" -> NyraScreen(
                        messages = nyraMessages,
                        isLoading = isNyraLoading,
                        onSendMessage = { text -> nyraEngine.sendMessage(text) },
                        onExecuteAction = { card -> nyraEngine.executeAction(card) }
                    )
                    "Profile" -> ProfileScreen(
                        userProfile = userProfile,
                        memories = memories,
                        sensorRepository = sensorRepository,
                        safetySettings = safetySettings,
                        onUpdateSafetySettings = { updated ->
                            safetySettings = updated
                            storageManager.saveSafetySettings(updated)
                            nyraEngine.setSafetySettings(updated)
                        },
                        onTriggerFakeCall = { showFakeCallScreen = true },
                        onTestTelegramAlert = { updatedSettings ->
                            coroutineScope.launch {
                                val res = telegramAlertManager.sendSosAlert(
                                    userProfile = userProfile,
                                    safetySettings = updatedSettings,
                                    reason = "Test Emergency SOS verification alert from Settings",
                                    healthSnapshot = liveSnapshot
                                )
                                Toast.makeText(context, res.responseMessage, Toast.LENGTH_LONG).show()
                            }
                        },
                        onDeleteMemory = { id -> memoryRepository.removeMemory(id) },
                        onClearMemories = { memoryRepository.clearAllMemories() },
                        onTogglePregnancyMode = { enabled ->
                            val updated = userProfile.copy(isPregnancyModeEnabled = enabled)
                            userProfile = updated
                            storageManager.saveUserProfile(updated)
                        }
                    )
                }
            }
        }
    }
}
