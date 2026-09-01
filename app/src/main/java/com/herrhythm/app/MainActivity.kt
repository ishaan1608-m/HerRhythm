package com.herrhythm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.herrhythm.app.data.*
import com.herrhythm.app.ui.screens.*
import com.herrhythm.app.ui.theme.HerRhythmTheme
import com.herrhythm.app.ui.theme.RosePrimary
import com.herrhythm.app.ui.theme.TextMuted
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private lateinit var storageManager: LocalStorageManager
    private lateinit var sensorRepository: HealthSensorRepository
    private lateinit var memoryRepository: UserMemoryRepository
    private lateinit var cycleRepository: CycleRepository
    private lateinit var fitnessRepository: FitnessRepository
    private lateinit var doctorRepository: DoctorRepository
    private lateinit var nyraEngine: NyraAgentEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageManager = LocalStorageManager(applicationContext)
        sensorRepository = MockWatchDataProvider()
        memoryRepository = UserMemoryRepository()
        
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
            healthRepository = sensorRepository
        )

        // If user profile is saved, load it into engine
        storageManager.getUserProfile()?.let { profile ->
            nyraEngine.setUserProfile(profile)
        }

        setContent {
            HerRhythmTheme {
                MainAppContainer(
                    storageManager = storageManager,
                    sensorRepository = sensorRepository,
                    memoryRepository = memoryRepository,
                    cycleRepository = cycleRepository,
                    fitnessRepository = fitnessRepository,
                    doctorRepository = doctorRepository,
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
    nyraEngine: NyraAgentEngine
) {
    // Persistent onboarding state
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
                fitnessGoal = "Energy & Recovery"
            )
        )
    }

    var selectedTab by remember { mutableStateOf("Home") }
    var activeRunningSession by remember { mutableStateOf<WorkoutSession?>(null) }
    var showLogPeriodDialog by remember { mutableStateOf(false) }
    var showGynaecologistScreen by remember { mutableStateOf(false) }

    val liveSnapshot by sensorRepository.liveSnapshot.collectAsState()
    val memories by memoryRepository.memories.collectAsState()
    val nyraMessages by nyraEngine.messages.collectAsState()
    val isNyraLoading by nyraEngine.isLoading.collectAsState()
    val activeReminders by nyraEngine.activeReminders.collectAsState()

    // Period log dialog overlay
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

    // Full screen Gynaecologist Directory overlay
    if (showGynaecologistScreen) {
        GynaecologistScreen(
            doctorRepository = doctorRepository,
            onBack = { showGynaecologistScreen = false }
        )
        return
    }

    // Full screen interactive workout session overlay
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

    // Onboarding flow (only shown once, saved to phone memory)
    if (!isOnboardingCompleted) {
        OnboardingScreen(
            onOnboardingFinished = { profile ->
                userProfile = profile
                storageManager.saveUserProfile(profile)
                storageManager.setOnboardingCompleted(true)
                nyraEngine.setUserProfile(profile)

                // Parse onboarding cycle start date
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
            bottomBar = {
                NavigationBar(
                    containerColor = com.herrhythm.app.ui.theme.CreamCard,
                    contentColor = RosePrimary
                ) {
                    val tabs = listOf(
                        "Home" to Icons.Default.Home,
                        "Health" to Icons.Default.Favorite,
                        "Fitness" to Icons.Default.FitnessCenter,
                        "NYRA" to Icons.Default.AutoAwesome,
                        "Profile" to Icons.Default.Person
                    )

                    tabs.forEach { (label, icon) ->
                        val isSelected = selectedTab == label
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = label },
                            icon = {
                                Icon(
                                    icon,
                                    contentDescription = label,
                                    tint = if (isSelected) RosePrimary else TextMuted
                                )
                            },
                            label = {
                                Text(
                                    label,
                                    color = if (isSelected) RosePrimary else TextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = com.herrhythm.app.ui.theme.SoftRose
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    "Home" -> HomeScreen(
                        userProfile = userProfile,
                        cycleInfo = cycleRepository.getCycleInfo(),
                        healthSnapshot = liveSnapshot,
                        reminders = activeReminders,
                        onOpenNyraChat = { selectedTab = "NYRA" },
                        onOpenHealthDetail = { selectedTab = "Health" },
                        onOpenWatchManager = { selectedTab = "Profile" },
                        onOpenGynaecologists = { showGynaecologistScreen = true },
                        onOpenLogPeriodDialog = { showLogPeriodDialog = true }
                    )
                    "Health" -> HealthScreen(
                        snapshot = liveSnapshot
                    )
                    "Fitness" -> FitnessScreen(
                        snapshot = liveSnapshot,
                        fitnessRepository = fitnessRepository,
                        onStartWorkout = { session ->
                            activeRunningSession = session
                        },
                        onGenerateWorkout = {
                            nyraEngine.sendMessage("Generate a custom 30 min workout plan for my current energy level")
                            selectedTab = "NYRA"
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
