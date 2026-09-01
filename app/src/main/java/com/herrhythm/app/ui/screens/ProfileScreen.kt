package com.herrhythm.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.HealthSensorRepository
import com.herrhythm.app.data.MemoryItem
import com.herrhythm.app.data.SafetySettings
import com.herrhythm.app.data.UserProfile
import com.herrhythm.app.ui.theme.*

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    memories: List<MemoryItem>,
    sensorRepository: HealthSensorRepository,
    safetySettings: SafetySettings = SafetySettings(),
    onUpdateSafetySettings: (SafetySettings) -> Unit = {},
    onTriggerFakeCall: () -> Unit = {},
    onTestTelegramAlert: (SafetySettings) -> Unit = {},
    onDeleteMemory: (String) -> Unit,
    onClearMemories: () -> Unit,
    onTogglePregnancyMode: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedSubSection by remember { mutableStateOf("Profile") } // Profile, Safety & SOS, Memories, WomenHealth, WatchDemo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PookieDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // User Profile Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PookieCardLight)
                    .border(2.dp, PookiePinkPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile.name.take(1).uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PookiePinkPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(userProfile.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("${userProfile.occupation} • ${userProfile.weightKg.toInt()} kg", fontSize = 13.sp, color = PookieTextMuted)
                Text("Goal: ${userProfile.fitnessGoal}", fontSize = 12.sp, color = PookiePinkGlow)
            }
        }

        // Sub-navigation selector tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PookieCardBg)
                .padding(4.dp)
        ) {
            listOf("Profile", "Safety & SOS", "Memories", "Women's Health", "Watch Demo").forEach { tab ->
                val isSelected = selectedSubSection == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PookiePinkPrimary else Color.Transparent)
                        .clickable { selectedSubSection = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else PookieTextMuted,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedSubSection) {
            "Profile" -> ProfileTabDetails(userProfile = userProfile)
            "Safety & SOS" -> SafetyTabDetails(
                settings = safetySettings,
                onUpdateSettings = onUpdateSafetySettings,
                onTriggerFakeCall = onTriggerFakeCall,
                onTestTelegramAlert = onTestTelegramAlert
            )
            "Memories" -> MemoriesTabDetails(memories = memories, onDeleteMemory = onDeleteMemory, onClearMemories = onClearMemories)
            "Women's Health" -> WomensHealthTabDetails(userProfile = userProfile, onTogglePregnancyMode = onTogglePregnancyMode)
            "Watch Demo" -> WatchDemoTabDetails(repository = sensorRepository)
        }
    }
}

// ─────────────────────────────────────────────
// SAFETY & SOS TAB
// ─────────────────────────────────────────────
@Composable
fun SafetyTabDetails(
    settings: SafetySettings,
    onUpdateSettings: (SafetySettings) -> Unit,
    onTriggerFakeCall: () -> Unit,
    onTestTelegramAlert: (SafetySettings) -> Unit
) {
    var fakeCallerName by remember { mutableStateOf(settings.fakeCallerName) }
    var fakeCallerNumber by remember { mutableStateOf(settings.fakeCallerNumber) }
    var botToken by remember { mutableStateOf(settings.telegramBotToken) }
    var chatId by remember { mutableStateOf(settings.telegramChatId) }
    var emergencyContactName by remember { mutableStateOf(settings.emergencyContactName) }
    var emergencyContactPhone by remember { mutableStateOf(settings.emergencyContactPhone) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Column {
        Text("Women Safety & Emergency Shield 🛡️", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Quick safe-exit fake calls and automatic Telegram SOS alerts with live location", fontSize = 12.sp, color = PookieTextMuted)

        Spacer(modifier = Modifier.height(16.dp))

        // 1. FAKE CALL CONFIGURATION
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = PookiePinkPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Safe Exit Fake Call Trigger", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Pressing this trigger rings your phone with an incoming call screen so you can excuse yourself and leave uncomfortable situations safely.",
                    fontSize = 12.sp,
                    color = PookieTextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fakeCallerName,
                    onValueChange = { fakeCallerName = it },
                    label = { Text("Caller Name on Screen", color = PookieTextMuted) },
                    placeholder = { Text("e.g. Bada Bhai ❤️, Mom, Papa") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = PookieCardLight,
                        unfocusedContainerColor = PookieCardLight
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fakeCallerNumber,
                    onValueChange = { fakeCallerNumber = it },
                    label = { Text("Caller Phone Number", color = PookieTextMuted) },
                    placeholder = { Text("e.g. +91 98765 43210") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = PookieCardLight,
                        unfocusedContainerColor = PookieCardLight
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onTriggerFakeCall,
                    colors = ButtonDefaults.buttonColors(containerColor = PookieCardLight),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("⚡ Test Fake Call Now", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. TELEGRAM EMERGENCY SOS BROADCAST
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Telegram SOS Live Location Alert", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "When you type \"unsafe\" / \"emergency\" in NYRA Chat or tap SOS, HerRhythm instantly fetches your GPS coordinates and broadcasts an SOS map alert to your Telegram chat/group.",
                    fontSize = 12.sp,
                    color = PookieTextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = botToken,
                    onValueChange = { botToken = it },
                    label = { Text("Telegram Bot Token", color = PookieTextMuted) },
                    placeholder = { Text("e.g. 123456789:ABCdefGhIJKlmNoPQ") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = PookieCardLight,
                        unfocusedContainerColor = PookieCardLight
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = chatId,
                    onValueChange = { chatId = it },
                    label = { Text("Telegram Chat ID / Group ID", color = PookieTextMuted) },
                    placeholder = { Text("e.g. 987654321 or -100123456789") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = PookieCardLight,
                        unfocusedContainerColor = PookieCardLight
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = emergencyContactPhone,
                    onValueChange = { emergencyContactPhone = it },
                    label = { Text("Emergency Phone Number", color = PookieTextMuted) },
                    placeholder = { Text("e.g. 112 or Family Mobile") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = PookieCardLight,
                        unfocusedContainerColor = PookieCardLight
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val updated = settings.copy(
                                fakeCallerName = fakeCallerName,
                                fakeCallerNumber = fakeCallerNumber,
                                telegramBotToken = botToken,
                                telegramChatId = chatId,
                                emergencyContactName = emergencyContactName,
                                emergencyContactPhone = emergencyContactPhone
                            )
                            onUpdateSettings(updated)
                            onTestTelegramAlert(updated)
                            statusMessage = "Test SOS message dispatched!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🚀 Test SOS Alert", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val updated = settings.copy(
                                fakeCallerName = fakeCallerName,
                                fakeCallerNumber = fakeCallerNumber,
                                telegramBotToken = botToken,
                                telegramChatId = chatId,
                                emergencyContactName = emergencyContactName,
                                emergencyContactPhone = emergencyContactPhone
                            )
                            onUpdateSettings(updated)
                            statusMessage = "Settings saved to phone memory! 💾"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Settings", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                if (statusMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = statusMessage!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PookiePinkGlow
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileTabDetails(userProfile: UserProfile) {
    Column {
        Text("Personal Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .padding(16.dp)
        ) {
            Column {
                ProfileInfoRow(label = "Full Name", value = userProfile.name)
                ProfileInfoRow(label = "Date of Birth", value = userProfile.dateOfBirth)
                ProfileInfoRow(label = "Weight", value = "${userProfile.weightKg.toInt()} kg")
                ProfileInfoRow(label = "Occupation", value = userProfile.occupation)
                ProfileInfoRow(label = "Work-Life Routine", value = userProfile.workLifeBalance)
                ProfileInfoRow(label = "Relationship", value = userProfile.relationshipStatus)
                ProfileInfoRow(label = "Health Conditions", value = if (userProfile.conditions.isEmpty()) "None" else userProfile.conditions.joinToString(", "))
                ProfileInfoRow(label = "Cycle Length", value = "${userProfile.cycleLengthDays} days (Period: ${userProfile.periodDurationDays} days)")
            }
        }
    }
}

@Composable
fun MemoriesTabDetails(
    memories: List<MemoryItem>,
    onDeleteMemory: (String) -> Unit,
    onClearMemories: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("NYRA Long-Term Memory", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            if (memories.isNotEmpty()) {
                TextButton(onClick = onClearMemories) {
                    Text("Clear All", color = HealthRed, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "NYRA remembers relevant details you mention so conversations stay personal.",
            fontSize = 12.sp,
            color = PookieTextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (memories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(PookieCardBg)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No memories stored yet. Chat with NYRA to build memory! 🌸", fontSize = 13.sp, color = PookieTextMuted)
            }
        } else {
            memories.forEach { memory ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PookieCardBg)
                        .padding(bottom = 8.dp)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(memory.category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PookiePinkGlow)
                            Text(memory.content, fontSize = 13.sp, color = Color.White)
                            Text(memory.dateAdded, fontSize = 10.sp, color = PookieTextMuted)
                        }
                        IconButton(onClick = { onDeleteMemory(memory.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PookieTextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WomensHealthTabDetails(userProfile: UserProfile, onTogglePregnancyMode: (Boolean) -> Unit) {
    var pregnancyMode by remember { mutableStateOf(userProfile.isPregnancyModeEnabled) }

    Column {
        Text("Women's Health Modes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pregnancy Mode 🤰", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Adapts cycle predictions for gestational tracking & kick counting", fontSize = 11.sp, color = PookieTextMuted)
                }
                Switch(
                    checked = pregnancyMode,
                    onCheckedChange = {
                        pregnancyMode = it
                        onTogglePregnancyMode(it)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PookiePinkPrimary)
                )
            }
        }
    }
}

@Composable
fun WatchDemoTabDetails(repository: HealthSensorRepository) {
    val snapshot by repository.liveSnapshot.collectAsState()

    Column {
        Text("Smartwatch Status & Sensor Simulator", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("HerRhythm Companion Watch", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(if (snapshot.isWatchConnected) "Connected via BLE (Simulated)" else "Disconnected", fontSize = 12.sp, color = if (snapshot.isWatchConnected) HealthGreen else HealthOrange)
                    }
                    Text("Battery: 88%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PookiePinkGlow)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Simulate Hardware Sensor Events:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PookieTextMuted)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { repository.triggerSimulatedStressEvent() },
                        colors = ButtonDefaults.buttonColors(containerColor = HealthOrange.copy(alpha = 0.8f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Stress Spike", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = { repository.triggerSimulatedWorkoutMode() },
                        colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Workout Mode", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = { repository.triggerSimulatedRestState() },
                        colors = ButtonDefaults.buttonColors(containerColor = HealthGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rest State", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = PookieTextMuted)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
