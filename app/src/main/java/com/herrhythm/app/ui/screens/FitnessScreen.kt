package com.herrhythm.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.*
import com.herrhythm.app.ui.theme.*

@Composable
fun FitnessScreen(
    snapshot: HealthSnapshot,
    fitnessRepository: FitnessRepository,
    onStartWorkout: (WorkoutSession) -> Unit,
    onGenerateWorkout: () -> Unit
) {
    val scrollState = rememberScrollState()
    val joinedPrograms by fitnessRepository.joinedPrograms.collectAsState()
    val allPrograms = fitnessRepository.allPrograms

    // Selected program to view details or start
    var selectedProgramId by remember { mutableStateOf("weight_loss") }
    val currentProgram = allPrograms.find { it.id == selectedProgramId } ?: allPrograms.first()
    val isCurrentProgramJoined = joinedPrograms.containsKey(selectedProgramId)
    val programProgress = joinedPrograms[selectedProgramId]
    val completedCount = programProgress?.completedSessionIds?.size ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Fitness & Programs", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Daily guided routines designed for women 🌸", fontSize = 13.sp, color = TextSecondary)
            }

            // Streak Badge
            val streak = programProgress?.streakDays ?: 1
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SoftRose)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$streak Day Streak 🔥", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RosePrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. ACTIVE PROGRAM HERO CARD
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(currentProgram.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(currentProgram.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${currentProgram.daysPerWeek} Days/Week • ${currentProgram.sessionDurationMin} Min Session", fontSize = 12.sp, color = RosePrimary)
                        }
                    }

                    if (!isCurrentProgramJoined) {
                        Button(
                            onClick = { fitnessRepository.joinProgram(currentProgram.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Join Program ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(HealthGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Active ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentProgram.description,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weekly Schedule Strip (Day 1..N)
                Text("Weekly Schedule", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    currentProgram.weeklyPlan.forEachIndexed { index, session ->
                        val isCompleted = programProgress?.completedSessionIds?.contains(session.id) == true
                        val isToday = index == (completedCount % currentProgram.weeklyPlan.size)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isCompleted -> HealthGreen.copy(alpha = 0.3f)
                                        isToday -> RosePrimary
                                        else -> CardSurface
                                    }
                                )
                                .border(
                                    width = if (isToday) 2.dp else 1.dp,
                                    color = if (isToday) RosePrimary else PeachBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onStartWorkout(session)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "D${index + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) Color.White else TextPrimary
                                )
                                Text(
                                    text = if (isCompleted) "✓" else if (isToday) "TODAY" else "${session.totalDurationMin}m",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isToday) SoftRose else TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Start Today's Session Button
                val todaySession = fitnessRepository.getTodaySessionForProgram(currentProgram.id)
                Button(
                    onClick = {
                        if (todaySession != null) {
                            onStartWorkout(todaySession)
                        } else if (currentProgram.weeklyPlan.isNotEmpty()) {
                            onStartWorkout(currentProgram.weeklyPlan.first())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Day ${(completedCount % currentProgram.weeklyPlan.size) + 1} Session (${currentProgram.sessionDurationMin} Mins) →",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. EXPLORE ALL FITNESS PROGRAMS
        Text("Explore Programs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Choose what matches your body's energy & goals 💕", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(allPrograms) { program ->
                val isSelected = program.id == selectedProgramId
                val isJoined = joinedPrograms.containsKey(program.id)

                ProgramCard(
                    program = program,
                    isSelected = isSelected,
                    isJoined = isJoined,
                    onSelect = { selectedProgramId = program.id },
                    onStart = {
                        if (program.weeklyPlan.isNotEmpty()) {
                            onStartWorkout(program.weeklyPlan.first())
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. DAILY ACTIVITY METRICS (STEPS, CALORIES, DISTANCE)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Today's Activity", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ActivityMetricRing(label = "Steps", current = "${snapshot.steps}", target = "8,000", color = RosePrimary)
                    ActivityMetricRing(label = "Calories", current = "${snapshot.caloriesBurned}", target = "450 kcal", color = DustyRose)
                    ActivityMetricRing(label = "Distance", current = "${snapshot.distanceKm} km", target = "5.0 km", color = HealthBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. NYRA AI WORKOUT GENERATOR
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RosePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ask NYRA for a Custom Workout", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Need a shorter routine or something light for cramps? NYRA can customize a plan on the fly.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryGradientButton(
                    text = "Ask NYRA for Tailored Plan ✨",
                    onClick = onGenerateWorkout,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ProgramCard(
    program: ExerciseProgram,
    isSelected: Boolean,
    isJoined: Boolean,
    onSelect: () -> Unit,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) CreamCard else WarmSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RosePrimary else PeachBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(program.emoji, fontSize = 24.sp)
                if (isJoined) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HealthGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Joined", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = program.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${program.daysPerWeek}d/wk • ${program.sessionDurationMin}m • ${program.difficulty}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = RosePrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            program.highlights.take(2).forEach { highlight ->
                Text(
                    text = highlight,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) RosePrimary else DustyRose
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Start Now →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ActivityMetricRing(label: String, current: String, target: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .border(3.dp, color, CircleShape)
                .background(CardSurface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(current, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(target, fontSize = 10.sp, color = TextMuted)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}
