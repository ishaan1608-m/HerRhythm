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
import com.herrhythm.app.data.UserProfile
import com.herrhythm.app.ui.theme.*

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    memories: List<MemoryItem>,
    sensorRepository: HealthSensorRepository,
    onDeleteMemory: (String) -> Unit,
    onClearMemories: () -> Unit,
    onTogglePregnancyMode: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedSubSection by remember { mutableStateOf("Profile") } // Profile, Memories, WomenHealth, WatchDemo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVioletBg)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // User Profile Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryMagenta.copy(alpha = 0.2f))
                    .border(2.dp, PrimaryMagenta, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile.name.take(1).uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryMagenta
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(userProfile.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("${userProfile.occupation} • ${userProfile.weightKg.toInt()} kg", fontSize = 13.sp, color = TextSecondary)
                Text("Goal: ${userProfile.fitnessGoal}", fontSize = 12.sp, color = RadiantPurple)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sub-navigation selector tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkPurpleCard)
                .padding(4.dp)
        ) {
            listOf("Profile", "Memories", "Women's Health", "Watch / Demo").forEach { tab ->
                val isSelected = selectedSubSection == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PrimaryMagenta else Color.Transparent)
                        .clickable { selectedSubSection = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (selectedSubSection) {
            "Profile" -> ProfileTabDetails(userProfile = userProfile)
            "Memories" -> MemoriesTabDetails(memories = memories, onDeleteMemory = onDeleteMemory, onClearAll = onClearMemories)
            "Women's Health" -> WomensHealthTabDetails(isPregnancyMode = userProfile.isPregnancyModeEnabled, onTogglePregnancyMode = onTogglePregnancyMode)
            "Watch / Demo" -> WatchDemoTabDetails(repository = sensorRepository)
        }
    }
}

@Composable
fun ProfileTabDetails(userProfile: UserProfile) {
    Column {
        Text("Account & Personalization", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileInfoRow(label = "Date of Birth", value = userProfile.dateOfBirth)
                ProfileInfoRow(label = "Language Preference", value = userProfile.preferredLanguage)
                ProfileInfoRow(label = "AI Companion Mode", value = userProfile.aiPersonality)
                ProfileInfoRow(label = "Primary Goal", value = userProfile.fitnessGoal)
            }
        }
    }
}

@Composable
fun MemoriesTabDetails(
    memories: List<MemoryItem>,
    onDeleteMemory: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("NYRA Contextual Memory Store", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            if (memories.isNotEmpty()) {
                TextButton(onClick = onClearAll) {
                    Text("Clear All", fontSize = 12.sp, color = PrimaryMagenta)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Memories automatically extracted by NYRA to personalize wellness guidance. You have 100% control.",
            fontSize = 12.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (memories.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No stored memories right now.", fontSize = 14.sp, color = TextMuted)
                }
            }
        } else {
            memories.forEach { memory ->
                GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RadiantPurple.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(memory.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RadiantPurple)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(memory.content, fontSize = 13.sp, color = TextPrimary)
                        }
                        IconButton(onClick = { onDeleteMemory(memory.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WomensHealthTabDetails(
    isPregnancyMode: Boolean,
    onTogglePregnancyMode: (Boolean) -> Unit
) {
    Column {
        Text("Women's Health & Cycle Tracker", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Cycle Phase", fontSize = 13.sp, color = TextMuted)
                        Text("Follicular Phase (Day 11)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryMagenta)
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryMagenta.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = PrimaryMagenta)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Next predicted period: In 17 days (Est. Sep 1)", fontSize = 13.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = GlassCardBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pregnancy Mode", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Adapts workouts, reminders & nutrition context for maternal care.", fontSize = 12.sp, color = TextMuted)
                    }
                    Switch(
                        checked = isPregnancyMode,
                        onCheckedChange = onTogglePregnancyMode,
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryMagenta, checkedTrackColor = RadiantPurple)
                    )
                }
            }
        }
    }
}

@Composable
fun WatchDemoTabDetails(repository: HealthSensorRepository) {
    val snapshot by repository.liveSnapshot.collectAsState()

    Column {
        Text("Smartwatch Status & Hackathon Demo Controls", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("HerRhythm Companion Watch", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(if (snapshot.isWatchConnected) "Connected via BLE (Simulated)" else "Disconnected", fontSize = 12.sp, color = if (snapshot.isWatchConnected) HealthGreen else HealthOrange)
                    }
                    Text("Battery: 88%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryMagenta)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Simulate Hardware Sensor Events (For Evaluators):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { repository.triggerSimulatedStressEvent() },
                        colors = ButtonDefaults.buttonColors(containerColor = HealthOrange.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Stress Spike", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = { repository.triggerSimulatedWorkoutMode() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryMagenta.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Workout Mode", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = { repository.triggerSimulatedRestState() },
                        colors = ButtonDefaults.buttonColors(containerColor = HealthGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rest State", fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { repository.setWatchConnected(!snapshot.isWatchConnected) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (snapshot.isWatchConnected) "Simulate Disconnection" else "Simulate BLE Reconnect",
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
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
        Text(label, fontSize = 13.sp, color = TextMuted)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
