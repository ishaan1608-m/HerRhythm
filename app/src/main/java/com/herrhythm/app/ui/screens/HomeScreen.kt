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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.CycleInfo
import com.herrhythm.app.data.HealthSnapshot
import com.herrhythm.app.data.PeriodCalculator
import com.herrhythm.app.data.UserProfile
import com.herrhythm.app.ui.theme.*
import java.time.LocalDate

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    cycleInfo: CycleInfo,
    healthSnapshot: HealthSnapshot,
    reminders: List<Pair<String, String>>,
    onOpenNyraChat: () -> Unit,
    onOpenHealthDetail: () -> Unit,
    onOpenWatchManager: () -> Unit,
    onOpenGynaecologists: () -> Unit,
    onOpenLogPeriodDialog: () -> Unit
) {
    val scrollState = rememberScrollState()
    val daysUntilNext = PeriodCalculator.getDaysUntilNextPeriod(cycleInfo)
    val pregnancyChance = PeriodCalculator.getChanceOfPregnancy(cycleInfo, LocalDate.now())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // 1. Top Header & Greeting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning, ${userProfile.name} 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Day ${cycleInfo.currentCycleDay} • ${cycleInfo.currentPhase.displayName}",
                    fontSize = 13.sp,
                    color = RosePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Watch Connection Indicator Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, PeachBorder, RoundedCornerShape(20.dp))
                    .background(CreamCard)
                    .clickable { onOpenWatchManager() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (healthSnapshot.isWatchConnected) HealthGreen else HealthOrange)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (healthSnapshot.isWatchConnected) "Watch Synced" else "Demo Mode",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. LIVE MENSTRUAL CYCLE RING HERO CARD
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌸", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(cycleInfo.currentPhase.displayName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RosePrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (daysUntilNext <= 0) "Period is expected today 💕"
                            else "Period in $daysUntilNext days (${cycleInfo.nextPeriodDate})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Pregnancy Chance: $pregnancyChance • Cycle Day ${cycleInfo.currentCycleDay}/${cycleInfo.cycleLengthDays}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    // Circular Phase Progress Badge
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(SoftRose, CardSurface)))
                            .border(3.dp, RosePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Day", fontSize = 10.sp, color = TextMuted)
                            Text("${cycleInfo.currentCycleDay}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RosePrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Phase description advice
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WarmSurface)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡 ${cycleInfo.currentPhase.description}",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Log Period & Symptoms Button
                Button(
                    onClick = onOpenLogPeriodDialog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Period / Daily Symptoms 🩸", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. GYNAECOLOGISTS & SPECIALISTS BANNER
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenGynaecologists() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(RosePrimary, DustyRose))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👩‍⚕️", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Consult Gynaecologists", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Online Video Calls & In-Clinic Bookings", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Text("Find →", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RosePrimary)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. PERSONALIZED CONDITION CARE (IF REPORTED)
        if (userProfile.conditions.isNotEmpty() && !userProfile.conditions.contains("None of these")) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Personalized Care for ${userProfile.conditions.first()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "We're tailoring nutrition, exercise intensity, and cramp relief routines specifically for your ${userProfile.conditions.joinToString(", ")} profile.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 5. NYRA DAILY AI INSIGHT CARD
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(RosePrimary, DustyRose))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("NYRA Daily Health Insight", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RosePrimary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "\"You slept ${healthSnapshot.sleepHours} hours with good HRV consistency (${healthSnapshot.hrv}ms). Your recovery score is ${healthSnapshot.recoveryScore}%. Let's keep today's workout moderate and stay hydrated!\"",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOpenNyraChat,
                    colors = ButtonDefaults.buttonColors(containerColor = SoftRose),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Chat with NYRA about your cycle →", color = RosePrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. TODAY'S HEALTH OVERVIEW GRID
        Text("Health Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            // Heart Rate & Recovery
            Column(modifier = Modifier.weight(1f)) {
                MetricCard(
                    title = "Heart Rate",
                    value = "${healthSnapshot.heartRate} bpm",
                    subtitle = "Resting: ${healthSnapshot.restingHeartRate} bpm",
                    icon = Icons.Default.Favorite,
                    accentColor = RosePrimary,
                    onClick = onOpenHealthDetail
                )
                Spacer(modifier = Modifier.height(12.dp))
                MetricCard(
                    title = "Stress Index",
                    value = "${healthSnapshot.edaStress}/100",
                    subtitle = if (healthSnapshot.edaStress < 40) "Calm & Balanced" else "Elevated Stress",
                    icon = Icons.Default.SelfImprovement,
                    accentColor = HealthBlue,
                    onClick = onOpenHealthDetail
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Sleep & Recovery
            Column(modifier = Modifier.weight(1f)) {
                MetricCard(
                    title = "Recovery Score",
                    value = "${healthSnapshot.recoveryScore}%",
                    subtitle = "Optimal Readiness",
                    icon = Icons.Default.BatteryChargingFull,
                    accentColor = HealthGreen,
                    onClick = onOpenHealthDetail
                )
                Spacer(modifier = Modifier.height(12.dp))
                MetricCard(
                    title = "Sleep Tracking",
                    value = "${healthSnapshot.sleepHours} hrs",
                    subtitle = "Score: ${healthSnapshot.sleepQualityScore}/100",
                    icon = Icons.Default.Bedtime,
                    accentColor = DustyRose,
                    onClick = onOpenHealthDetail
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7. TODAY'S REMINDERS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Today's Plan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("${reminders.size} Scheduled", fontSize = 12.sp, color = TextMuted)
        }
        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                reminders.forEachIndexed { idx, (title, time) ->
                    PlanItemRow(title = title, time = time, isLast = idx == reminders.size - 1)
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 13.sp, color = TextSecondary)
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 11.sp, color = TextMuted)
        }
    }
}

@Composable
fun PlanItemRow(title: String, time: String, isLast: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(time, fontSize = 12.sp, color = TextMuted)
        }
    }
    if (!isLast) {
        HorizontalDivider(color = PeachBorder, thickness = 0.8.dp)
    }
}
