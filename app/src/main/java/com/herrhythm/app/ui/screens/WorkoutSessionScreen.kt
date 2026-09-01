package com.herrhythm.app.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.Exercise
import com.herrhythm.app.data.WorkoutSession
import com.herrhythm.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WorkoutSessionScreen(
    session: WorkoutSession,
    onClose: () -> Unit,
    onSessionCompleted: (Int) -> Unit // returns total elapsed seconds
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isResting by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var totalElapsedSeconds by remember { mutableIntStateOf(0) }

    val currentExercise = session.exercises.getOrNull(currentIndex)
    var timeLeft by remember(currentIndex, isResting) {
        mutableIntStateOf(
            if (isResting) {
                currentExercise?.restAfterSeconds ?: 15
            } else {
                currentExercise?.durationSeconds ?: 30
            }
        )
    }

    // Active Timer loop
    LaunchedEffect(isPaused, isFinished, isResting, currentIndex) {
        while (!isPaused && !isFinished) {
            delay(1000)
            totalElapsedSeconds++
            if (timeLeft > 1) {
                timeLeft--
            } else {
                // Time up for current step
                if (!isResting && (currentExercise?.restAfterSeconds ?: 0) > 0 && currentIndex < session.exercises.size - 1) {
                    // Switch to rest
                    isResting = true
                    timeLeft = currentExercise?.restAfterSeconds ?: 15
                } else {
                    // Move to next exercise or finish
                    if (currentIndex < session.exercises.size - 1) {
                        currentIndex++
                        isResting = false
                        timeLeft = session.exercises[currentIndex].durationSeconds
                    } else {
                        isFinished = true
                    }
                }
            }
        }
    }

    if (isFinished) {
        SessionCompleteScreen(
            session = session,
            totalElapsedSeconds = totalElapsedSeconds,
            onDone = {
                onSessionCompleted(totalElapsedSeconds)
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(16.dp)
    ) {
        // Top Bar: Back/Close button + Progress Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CardSurface)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Exercise ${currentIndex + 1} of ${session.exercises.size}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            // Pause / Resume button
            IconButton(
                onClick = { isPaused = !isPaused },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isPaused) HealthGreen.copy(alpha = 0.2f) else CardSurface)
            ) {
                Icon(
                    if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    tint = if (isPaused) HealthGreen else RosePrimary
                )
            }
        }

        // Progress bar across full session
        val progress = (currentIndex.toFloat() + 0.1f) / session.exercises.size.toFloat()
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = RosePrimary,
            trackColor = PeachBorder
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isResting) {
            // REST SCREEN
            val nextExercise = session.exercises.getOrNull(currentIndex + 1)
            RestView(
                secondsLeft = timeLeft,
                nextExercise = nextExercise,
                onSkipRest = {
                    if (currentIndex < session.exercises.size - 1) {
                        currentIndex++
                        isResting = false
                        timeLeft = session.exercises[currentIndex].durationSeconds
                    } else {
                        isFinished = true
                    }
                }
            )
        } else if (currentExercise != null) {
            // ACTIVE EXERCISE SCREEN
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Exercise Visual
                ExerciseAnimationCanvas(
                    exerciseType = currentExercise.type,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Exercise Title & Muscle Badges
                Text(
                    text = currentExercise.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    currentExercise.targetMuscles.forEach { muscle ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftRose)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = muscle.name.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RosePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Countdown Timer Ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(RosePrimary, DustyRose))),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$timeLeft",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "SEC",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftRose
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step-by-Step Instructions
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "📋 How to do it:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        currentExercise.instructions.forEachIndexed { idx, step ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text(
                                    text = "${idx + 1}. ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RosePrimary
                                )
                                Text(
                                    text = step,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        if (currentExercise.tips.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WarmSurface)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "💡 Tip: ${currentExercise.tips}",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                IconButton(
                    onClick = {
                        if (currentIndex > 0) {
                            currentIndex--
                            isResting = false
                            timeLeft = session.exercises[currentIndex].durationSeconds
                        }
                    },
                    enabled = currentIndex > 0,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = TextPrimary)
                }

                // Complete / Next Button
                Button(
                    onClick = {
                        if (currentIndex < session.exercises.size - 1) {
                            isResting = true
                            timeLeft = currentExercise.restAfterSeconds
                        } else {
                            isFinished = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (currentIndex == session.exercises.size - 1) "Finish Workout 🏆" else "Next Exercise →",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Skip button
                IconButton(
                    onClick = {
                        if (currentIndex < session.exercises.size - 1) {
                            currentIndex++
                            isResting = false
                            timeLeft = session.exercises[currentIndex].durationSeconds
                        } else {
                            isFinished = true
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Skip", tint = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun RestView(
    secondsLeft: Int,
    nextExercise: Exercise?,
    onSkipRest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("☕ Rest Time", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = RosePrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Catch your breath and hydrate 🌸", fontSize = 13.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(24.dp))

        // Big countdown
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(CardSurface)
                .border(3.dp, RosePrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$secondsLeft",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = RosePrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (nextExercise != null) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("COMING UP NEXT:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(nextExercise.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("${nextExercise.durationSeconds} seconds", fontSize = 12.sp, color = RosePrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onSkipRest,
            shape = RoundedCornerShape(14.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(RosePrimary, DustyRose)))
        ) {
            Text("Skip Rest →", color = RosePrimary, fontWeight = FontWeight.Bold)
        }
    }
}
