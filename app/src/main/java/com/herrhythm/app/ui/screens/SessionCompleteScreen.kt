package com.herrhythm.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.WorkoutSession
import com.herrhythm.app.ui.theme.*

@Composable
fun SessionCompleteScreen(
    session: WorkoutSession,
    totalElapsedSeconds: Int,
    onDone: () -> Unit
) {
    val scaleTransition = rememberInfiniteTransition(label = "badgePulse")
    val scale by scaleTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val minutes = totalElapsedSeconds / 60
    val seconds = totalElapsedSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Badge celebration
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(RosePrimary, DustyRose)))
                    .border(3.dp, SoftRose, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🎉", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Workout Complete!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Awesome work, gorgeous! You showed up for yourself today 💕",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Stats Card
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = session.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = RosePrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBox(
                        value = formattedTime,
                        label = "Duration",
                        icon = "⏱️"
                    )
                    StatBox(
                        value = "${session.estimatedCalories}",
                        label = "Calories Burned",
                        icon = "🔥"
                    )
                    StatBox(
                        value = "${session.exercises.size}",
                        label = "Exercises",
                        icon = "💪"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarmSurface)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌸 Streak +1 Day! Kept in sync with your rhythm.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }
        }

        // Done button
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RosePrimary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Save & Finish ✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}
