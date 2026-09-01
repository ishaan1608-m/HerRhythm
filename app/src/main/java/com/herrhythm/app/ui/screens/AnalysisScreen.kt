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
import com.herrhythm.app.data.CycleInfo
import com.herrhythm.app.data.HealthSnapshot
import com.herrhythm.app.data.UserProfile
import com.herrhythm.app.ui.theme.*

@Composable
fun AnalysisScreen(
    userProfile: UserProfile,
    cycleInfo: CycleInfo,
    healthSnapshot: HealthSnapshot,
    onOpenSettings: () -> Unit,
    onOpenLogPeriod: () -> Unit,
    onTogglePregnancy: (Boolean) -> Unit,
    onUpdateWeight: (Float) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedGoal by remember { mutableStateOf("Track my period") }
    var isPregnancyEnabled by remember { mutableStateOf(userProfile.isPregnancyModeEnabled) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var currentWeight by remember { mutableFloatStateOf(userProfile.weightKg) }

    var currentTemp by remember { mutableFloatStateOf(36.6f) }
    var showTempDialog by remember { mutableStateOf(false) }

    // Weight Dialog
    if (showWeightDialog) {
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Update Weight ⚖️", fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentWeight = (currentWeight - 0.5f).coerceAtLeast(30f) }) {
                        Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${String.format("%.1f", currentWeight)} kg",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PookiePinkPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(onClick = { currentWeight = (currentWeight + 0.5f).coerceAtMost(150f) }) {
                        Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateWeight(currentWeight)
                        showWeightDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Temperature Dialog
    if (showTempDialog) {
        AlertDialog(
            onDismissRequest = { showTempDialog = false },
            title = { Text("Log Basal Body Temperature 🌡️", fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentTemp = (currentTemp - 0.1f).coerceAtLeast(35.0f) }) {
                        Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${String.format("%.1f", currentTemp)} °C",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PookiePinkPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(onClick = { currentTemp = (currentTemp + 0.1f).coerceAtMost(42.0f) }) {
                        Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTempDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTempDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PookieDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ─────────────────────────────────────────────
        // 1. TOP BAR: GEAR + TITLE "Analysis"
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PookieCardBg)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(22.dp))
            }

            Text(
                text = "Analysis",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ─────────────────────────────────────────────
        // 2. TOP ACTION BUTTONS ROW (Settings, Reminders, Theme, Feedback)
        // ─────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(
                Triple("Settings", Icons.Default.Settings, onOpenSettings),
                Triple("Reminders", Icons.Default.Notifications, onOpenSettings),
                Triple("Theme", Icons.Default.Palette, onOpenSettings),
                Triple("Feedback", Icons.Default.Send, onOpenSettings)
            ).forEach { (label, icon, onClick) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PookieCardBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = label, tint = PookieLavender, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(label, fontSize = 11.sp, color = PookieTextMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ─────────────────────────────────────────────
        // 3. "MY GOAL" SECTION & PREGNANCY SWITCH
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PookiePinkCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("My goal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Horizontal Goal Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Track my period", "Try to conceive", "Track pregnancy").forEach { goal ->
                        val isSelected = selectedGoal == goal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) PookieLavenderCard else PookieCardLight)
                                .clickable { selectedGoal = goal }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = goal,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PookieLavenderText else PookieTextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Prediction settings row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenLogPeriod() }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔄", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Prediction settings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("For period, cycle, and ovulation", fontSize = 11.sp, color = PookieTextMuted)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PookieTextMuted)
                }

                HorizontalDivider(color = PookieCardLight, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Pregnancy toggle row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤰", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Pregnancy Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Switch(
                        checked = isPregnancyEnabled,
                        onCheckedChange = {
                            isPregnancyEnabled = it
                            onTogglePregnancy(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PookiePinkPrimary,
                            uncheckedThumbColor = PookieTextMuted,
                            uncheckedTrackColor = PookieCardLight
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 4. "CYCLE ANALYSIS" CARDS (AVERAGE PERIOD & AVERAGE CYCLE)
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭕", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cycle analysis", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PookieTextMuted)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left Pink Card: Average Period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(105.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(PookiePinkCard)
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text("💧", fontSize = 16.sp)
                            }
                            Column {
                                Text(
                                    text = "${cycleInfo.periodDurationDays} Days",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PookiePinkText
                                )
                                Text("Average period", fontSize = 11.sp, color = PookiePinkText.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Right Yellow Card: Average Cycle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(105.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(PookiePastelYellow)
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text("⭕", fontSize = 16.sp)
                            }
                            Column {
                                Text(
                                    text = "${cycleInfo.cycleLengthDays} Days",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PookieYellowText
                                )
                                Text("Average cycle", fontSize = 11.sp, color = PookieYellowText.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Log period helper note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PookieCardLight)
                        .clickable { onOpenLogPeriod() }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Last logged cycle: Day ${cycleInfo.currentCycleDay}/${cycleInfo.cycleLengthDays}",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Text("Log period", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PookiePinkPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 5. "WEIGHT" CARD
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PookieLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Scale, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Weight", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PookieTextMuted)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${String.format("%.1f", currentWeight)} kg",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showWeightDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PookieCardLight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add weight", color = PookieLavender, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 6. "TEMPERATURE" CARD
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PookiePinkPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Thermostat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Temperature", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PookieTextMuted)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${String.format("%.1f", currentTemp)} °C",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showTempDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PookieCardLight),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add temperature", color = PookiePinkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
