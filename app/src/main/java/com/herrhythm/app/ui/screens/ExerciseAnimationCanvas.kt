package com.herrhythm.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.ExerciseType
import com.herrhythm.app.ui.theme.*
import kotlin.math.sin

@Composable
fun ExerciseAnimationCanvas(
    exerciseType: ExerciseType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "exerciseAnimation")
    
    // Smooth repeating progress between 0f and 1f and back to 0f
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "exerciseCycle"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val strokeWidth = 8.dp.toPx()
            val headRadius = 14.dp.toPx()
            val primaryColor = RosePrimary
            val accentColor = DustyRose
            val groundColor = PeachBorder

            // Draw ground line
            val groundY = size.height * 0.85f
            drawLine(
                color = groundColor,
                start = Offset(size.width * 0.1f, groundY),
                end = Offset(size.width * 0.9f, groundY),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            when (exerciseType) {
                ExerciseType.SQUAT, ExerciseType.JUMP_SQUAT -> {
                    // Standing -> Squat down
                    val squatDepth = progress * 40.dp.toPx()
                    val headY = size.height * 0.28f + squatDepth
                    val torsoTop = headY + headRadius + 4.dp.toPx()
                    val hipsY = torsoTop + 38.dp.toPx()
                    
                    // Head
                    drawCircle(color = primaryColor, radius = headRadius, center = Offset(center.x, headY))
                    // Torso
                    drawLine(color = primaryColor, start = Offset(center.x, torsoTop), end = Offset(center.x, hipsY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    // Arms (extended forward when squatting)
                    val armEndY = torsoTop + 10.dp.toPx() - (progress * 8.dp.toPx())
                    val armEndX = center.x + 35.dp.toPx() + (progress * 15.dp.toPx())
                    drawLine(color = accentColor, start = Offset(center.x, torsoTop + 6.dp.toPx()), end = Offset(armEndX, armEndY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    
                    // Legs (bending outward)
                    val kneeXOffset = 22.dp.toPx() + (progress * 16.dp.toPx())
                    val kneeY = hipsY + 24.dp.toPx()
                    val footY = groundY - 4.dp.toPx()
                    
                    // Left leg
                    drawLine(color = primaryColor, start = Offset(center.x, hipsY), end = Offset(center.x - kneeXOffset, kneeY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = primaryColor, start = Offset(center.x - kneeXOffset, kneeY), end = Offset(center.x - 22.dp.toPx(), footY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    // Right leg
                    drawLine(color = primaryColor, start = Offset(center.x, hipsY), end = Offset(center.x + kneeXOffset, kneeY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = primaryColor, start = Offset(center.x + kneeXOffset, kneeY), end = Offset(center.x + 22.dp.toPx(), footY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                }

                ExerciseType.JUMPING_JACK, ExerciseType.SPRINT_IN_PLACE, ExerciseType.HIGH_KNEES -> {
                    // Jumping jacks: arms up/down, legs apart/together
                    val headY = size.height * 0.25f - (progress * 12.dp.toPx())
                    val torsoTop = headY + headRadius + 4.dp.toPx()
                    val hipsY = torsoTop + 40.dp.toPx()

                    drawCircle(color = primaryColor, radius = headRadius, center = Offset(center.x, headY))
                    drawLine(color = primaryColor, start = Offset(center.x, torsoTop), end = Offset(center.x, hipsY), strokeWidth = strokeWidth, cap = StrokeCap.Round)

                    // Arms angle
                    val armAngle = (1f - progress) * 1.8f - 0.9f
                    val armLen = 35.dp.toPx()
                    val leftArmEnd = Offset(center.x - armLen * kotlin.math.cos(armAngle), torsoTop - armLen * kotlin.math.sin(armAngle))
                    val rightArmEnd = Offset(center.x + armLen * kotlin.math.cos(armAngle), torsoTop - armLen * kotlin.math.sin(armAngle))
                    drawLine(color = accentColor, start = Offset(center.x, torsoTop + 6.dp.toPx()), end = leftArmEnd, strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = accentColor, start = Offset(center.x, torsoTop + 6.dp.toPx()), end = rightArmEnd, strokeWidth = strokeWidth, cap = StrokeCap.Round)

                    // Legs
                    val legSpread = 12.dp.toPx() + (progress * 32.dp.toPx())
                    val footY = groundY - 4.dp.toPx()
                    drawLine(color = primaryColor, start = Offset(center.x, hipsY), end = Offset(center.x - legSpread, footY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = primaryColor, start = Offset(center.x, hipsY), end = Offset(center.x + legSpread, footY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                }

                ExerciseType.PUSH_UP, ExerciseType.PLANK, ExerciseType.SIDE_PLANK, ExerciseType.MOUNTAIN_CLIMBER -> {
                    // Horizontal push up / plank animation
                    val pushUpProgress = progress
                    val headX = size.width * 0.28f
                    val headY = groundY - 28.dp.toPx() + (pushUpProgress * 18.dp.toPx())
                    val feetX = size.width * 0.78f
                    val feetY = groundY - 6.dp.toPx()

                    val shoulderX = headX + 16.dp.toPx()
                    val shoulderY = headY + 6.dp.toPx()

                    drawCircle(color = primaryColor, radius = headRadius, center = Offset(headX, headY))
                    // Body line
                    drawLine(color = primaryColor, start = Offset(shoulderX, shoulderY), end = Offset(feetX, feetY), strokeWidth = strokeWidth, cap = StrokeCap.Round)

                    // Arms
                    val handX = shoulderX + 4.dp.toPx()
                    val handY = groundY - 4.dp.toPx()
                    val elbowX = (shoulderX + handX) / 2f + (pushUpProgress * 14.dp.toPx())
                    val elbowY = (shoulderY + handY) / 2f
                    drawLine(color = accentColor, start = Offset(shoulderX, shoulderY), end = Offset(elbowX, elbowY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = accentColor, start = Offset(elbowX, elbowY), end = Offset(handX, handY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                }

                ExerciseType.LUNGE -> {
                    // Front and back leg lunge
                    val lungeDepth = progress * 24.dp.toPx()
                    val headY = size.height * 0.32f + lungeDepth
                    val torsoTop = headY + headRadius + 4.dp.toPx()
                    val hipsY = torsoTop + 36.dp.toPx()

                    drawCircle(color = primaryColor, radius = headRadius, center = Offset(center.x, headY))
                    drawLine(color = primaryColor, start = Offset(center.x, torsoTop), end = Offset(center.x, hipsY), strokeWidth = strokeWidth, cap = StrokeCap.Round)

                    // Front leg (bent at 90)
                    val frontKneeX = center.x + 30.dp.toPx()
                    val frontKneeY = hipsY + 16.dp.toPx() + (progress * 8.dp.toPx())
                    val frontFootX = frontKneeX
                    val frontFootY = groundY - 4.dp.toPx()
                    drawLine(color = primaryColor, start = Offset(center.x, hipsY), end = Offset(frontKneeX, frontKneeY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = primaryColor, start = Offset(frontKneeX, frontKneeY), end = Offset(frontFootX, frontFootY), strokeWidth = strokeWidth, cap = StrokeCap.Round)

                    // Back leg
                    val backKneeX = center.x - 22.dp.toPx()
                    val backKneeY = groundY - 14.dp.toPx() + (progress * 8.dp.toPx())
                    val backFootX = center.x - 38.dp.toPx()
                    val backFootY = groundY - 4.dp.toPx()
                    drawLine(color = accentColor, start = Offset(center.x, hipsY), end = Offset(backKneeX, backKneeY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = accentColor, start = Offset(backKneeX, backKneeY), end = Offset(backFootX, backFootY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                }

                ExerciseType.GLUTE_BRIDGE -> {
                    // Lying on back, lifting hips
                    val hipLift = progress * 32.dp.toPx()
                    val headX = size.width * 0.25f
                    val headY = groundY - 10.dp.toPx()
                    val shoulderX = headX + 18.dp.toPx()
                    val shoulderY = groundY - 12.dp.toPx()
                    val hipsX = center.x + 10.dp.toPx()
                    val hipsY = (groundY - 12.dp.toPx()) - hipLift
                    val feetX = size.width * 0.72f
                    val feetY = groundY - 4.dp.toPx()
                    val kneeX = hipsX + 22.dp.toPx()
                    val kneeY = hipsY - 10.dp.toPx()

                    drawCircle(color = primaryColor, radius = headRadius, center = Offset(headX, headY))
                    // Torso/Back
                    drawLine(color = primaryColor, start = Offset(shoulderX, shoulderY), end = Offset(hipsX, hipsY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    // Upper leg
                    drawLine(color = primaryColor, start = Offset(hipsX, hipsY), end = Offset(kneeX, kneeY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    // Lower leg
                    drawLine(color = primaryColor, start = Offset(kneeX, kneeY), end = Offset(feetX, feetY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                }

                ExerciseType.CAT_COW, ExerciseType.CHILD_POSE, ExerciseType.COBRA, ExerciseType.DOWNWARD_DOG, ExerciseType.WARRIOR_I -> {
                    // Gentle undulating spine for Yoga
                    val arch = (progress - 0.5f) * 20.dp.toPx()
                    val headX = size.width * 0.3f
                    val headY = groundY - 32.dp.toPx() + arch * 0.8f
                    val shoulderX = headX + 16.dp.toPx()
                    val shoulderY = groundY - 30.dp.toPx()
                    val hipsX = size.width * 0.68f
                    val hipsY = groundY - 30.dp.toPx() - arch * 0.5f

                    drawCircle(color = primaryColor, radius = headRadius, center = Offset(headX, headY))
                    
                    // Curved spine
                    val spinePath = Path().apply {
                        moveTo(shoulderX, shoulderY)
                        quadraticBezierTo(center.x, groundY - 30.dp.toPx() + arch, hipsX, hipsY)
                    }
                    drawPath(path = spinePath, color = primaryColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

                    // Front arms
                    drawLine(color = accentColor, start = Offset(shoulderX, shoulderY), end = Offset(shoulderX, groundY - 4.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    // Back legs (knees on floor)
                    drawLine(color = accentColor, start = Offset(hipsX, hipsY), end = Offset(hipsX + 4.dp.toPx(), groundY - 4.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                }

                else -> {
                    // Default rhythmic breathing figure
                    val breatheOffset = sin(progress * Math.PI.toFloat()) * 8.dp.toPx()
                    val headY = size.height * 0.3f - breatheOffset
                    val torsoTop = headY + headRadius + 4.dp.toPx()
                    val hipsY = torsoTop + 40.dp.toPx()

                    drawCircle(color = primaryColor, radius = headRadius, center = Offset(center.x, headY))
                    drawLine(color = primaryColor, start = Offset(center.x, torsoTop), end = Offset(center.x, hipsY), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    // Arms
                    drawLine(color = accentColor, start = Offset(center.x, torsoTop + 6.dp.toPx()), end = Offset(center.x - 25.dp.toPx(), torsoTop + 30.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = accentColor, start = Offset(center.x, torsoTop + 6.dp.toPx()), end = Offset(center.x + 25.dp.toPx(), torsoTop + 30.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    // Legs
                    drawLine(color = primaryColor, start = Offset(center.x, hipsY), end = Offset(center.x - 18.dp.toPx(), groundY - 4.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                    drawLine(color = primaryColor, start = Offset(center.x, hipsY), end = Offset(center.x + 18.dp.toPx(), groundY - 4.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                }
            }
        }

        // Animated Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(12.dp))
                .background(CreamBg.copy(alpha = 0.9f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "✨ Form Guide",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = RosePrimary
            )
        }
    }
}
