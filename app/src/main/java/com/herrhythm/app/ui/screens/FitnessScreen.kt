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
import androidx.compose.ui.draw.shadow
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
            .background(PookieDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ─────────────────────────────────────────────
        // 1. HEADER
        // ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Health & Fitness Programs", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Guided workouts & routines designed for women 🌸", fontSize = 12.sp, color = PookieTextMuted)
            }

            // Streak Badge
            val streak = programProgress?.streakDays ?: 1
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PookieCardBg)
                    .border(1.dp, PookiePinkPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = PookiePinkPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$streak Day Streak 🔥", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PookiePinkPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ─────────────────────────────────────────────
        // 2. ACTIVE PROGRAM HERO CARD
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
                        Text(currentProgram.emoji, fontSize = 30.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(currentProgram.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${currentProgram.daysPerWeek} Days/Week • ${currentProgram.sessionDurationMin} Mins/Session", fontSize = 12.sp, color = PookiePinkGlow)
                        }
                    }

                    if (!isCurrentProgramJoined) {
                        Button(
                            onClick = { fitnessRepository.joinProgram(currentProgram.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
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
                            Text("Active ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HealthGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentProgram.description,
                    fontSize = 12.sp,
                    color = PookieTextMuted,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weekly Schedule Strip (Day 1..N)
                Text("Weekly Schedule", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                                        isCompleted -> HealthGreen.copy(alpha = 0.25f)
                                        isToday -> PookiePinkPrimary
                                        else -> PookieCardLight
                                    }
                                )
                                .border(
                                    width = if (isToday) 2.dp else 1.dp,
                                    color = if (isToday) PookiePinkGlow else Color.Transparent,
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
                                    color = Color.White
                                )
                                Text(
                                    text = if (isCompleted) "✓" else if (isToday) "TODAY" else "${session.totalDurationMin}m",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isToday) Color.White else PookieTextMuted
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
                    colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
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

        Spacer(modifier = Modifier.height(22.dp))

        // ─────────────────────────────────────────────
        // 3. EXPLORE ALL FITNESS PROGRAMS (HORIZONTALLY SCROLLABLE CARDS)
        // ─────────────────────────────────────────────
        Text("Explore All Programs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Choose what matches your body's energy & phase 💕", fontSize = 12.sp, color = PookieTextMuted)
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

        // ─────────────────────────────────────────────
        // 4. DAILY ACTIVITY METRICS (STEPS, CALORIES, DISTANCE)
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Text("Today's Activity & Calorie Burn", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ActivityMetricRing(label = "Steps", current = "${snapshot.steps}", target = "8,000", color = PookiePinkPrimary)
                    ActivityMetricRing(label = "Calories", current = "${snapshot.caloriesBurned}", target = "450 kcal", color = PookieLavender)
                    ActivityMetricRing(label = "Distance", current = "${snapshot.distanceKm} km", target = "5.0 km", color = HealthGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ─────────────────────────────────────────────
        // 5. NYRA AI WORKOUT GENERATOR
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
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PookiePinkGlow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ask NYRA for a Custom Workout", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Need a quick 10-minute session, cramp-relief yoga, or low-energy stretch? NYRA generates tailored workouts on the fly.",
                    fontSize = 12.sp,
                    color = PookieTextMuted,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onGenerateWorkout,
                    colors = ButtonDefaults.buttonColors(containerColor = PookieLavender),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ask NYRA for Tailored Plan ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
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
            .background(if (isSelected) PookieCardLight else PookieCardBg)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PookiePinkPrimary else Color.White.copy(alpha = 0.1f),
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
                Text(program.emoji, fontSize = 26.sp)
                if (isJoined) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HealthGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Joined", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HealthGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = program.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${program.daysPerWeek}d/wk • ${program.sessionDurationMin}m • ${program.difficulty}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PookiePinkGlow
            )

            Spacer(modifier = Modifier.height(8.dp))

            program.highlights.take(2).forEach { highlight ->
                Text(
                    text = highlight,
                    fontSize = 11.sp,
                    color = PookieTextMuted,
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
                    containerColor = if (isSelected) PookiePinkPrimary else PookieLavender
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
                .background(PookieCardLight),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(current, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(target, fontSize = 9.sp, color = PookieTextMuted)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
