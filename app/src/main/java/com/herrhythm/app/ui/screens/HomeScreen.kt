package com.herrhythm.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.R
import com.herrhythm.app.data.CycleInfo
import com.herrhythm.app.data.HealthSnapshot
import com.herrhythm.app.data.PeriodCalculator
import com.herrhythm.app.data.UserProfile
import com.herrhythm.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    cycleInfo: CycleInfo,
    healthSnapshot: HealthSnapshot,
    reminders: List<Pair<String, String>>,
    onOpenNyraChat: () -> Unit,
    onOpenHealthDetail: () -> Unit,
    onOpenFitness: () -> Unit,
    onOpenWatchManager: () -> Unit,
    onOpenGynaecologists: () -> Unit,
    onOpenLogPeriodDialog: () -> Unit,
    onTriggerFakeCall: () -> Unit = {},
    onTriggerSos: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val daysUntilNext = PeriodCalculator.getDaysUntilNextPeriod(cycleInfo)
    val pregnancyChance = PeriodCalculator.getChanceOfPregnancy(cycleInfo, LocalDate.now())

    // Format dates for fertility & ovulation cards
    val ovulationDate = cycleInfo.nextPeriodDate.minusDays(14)
    val fertilityStart = ovulationDate.minusDays(4)
    val fertilityEnd = ovulationDate.plusDays(1)
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM")

    // Interactive speech bubble for the cute pookie cat mascot
    var catQuoteIndex by remember { mutableIntStateOf(0) }
    val catQuotes = listOf(
        "You're doing amazing, bestie! 🌸",
        "Don't forget to drink water! 💧",
        "Rest and be kind to yourself 💕",
        "Purr... NYRA & I are here for you! ✨",
        "Listen to your body today 🌸"
    )

    // Gentle floating animation for Pookie Cat
    val infiniteTransition = rememberInfiniteTransition(label = "catFloat")
    val catBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "catBounce"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PookieDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ─────────────────────────────────────────────
        // 1. TOP HEADER (SETTINGS GEAR + PREGNANCY CHANCE BADGE + NYRA AI)
        // ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onOpenWatchManager,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PookieCardBg)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(22.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pregnancy Chance Indicator Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PookieCardBg)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$pregnancyChance chance of pregnancy",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // NYRA AI Chat Button with Sparkles
                IconButton(
                    onClick = onOpenNyraChat,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(PookiePinkPrimary, PookieLavender))
                        )
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "NYRA AI", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ─────────────────────────────────────────────
        // 2. HERO SECTION (PERIOD DAYS LEFT + POOKIE CAT MASCOT)
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(PookieCardLight.copy(alpha = 0.8f), PookieCardBg),
                        radius = 600f
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left text & "Period Starts" action button
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Period",
                        fontSize = 15.sp,
                        color = PookieTextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (daysUntilNext <= 0) "TODAY" else "$daysUntilNext DAYS LEFT",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${cycleInfo.nextPeriodDate.format(DateTimeFormatter.ofPattern("dd MMM"))} - Next Period",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // "Period Starts" Hot Pink Pill Button
                    Button(
                        onClick = onOpenLogPeriodDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp),
                        modifier = Modifier.shadow(8.dp, RoundedCornerShape(24.dp), spotColor = PookiePinkPrimary)
                    ) {
                        Text(
                            text = "Period Starts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Right Cute Pookie Cat Mascot Companion
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(y = catBounce.dp)
                        .clickable {
                            catQuoteIndex = (catQuoteIndex + 1) % catQuotes.size
                        }
                ) {
                    // Floating speech bubble
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = catQuotes[catQuoteIndex],
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PookieTextDark,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .size(115.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pookie_cat),
                            contentDescription = "Cute Pookie Cat Companion",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 3. 3 HORIZONTAL CYCLE CARDS (CYCLE DAY, FERTILITY, OVULATION)
        // ─────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: CYCLE DAY with circular progress ring
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PookieLavenderCard)
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "CYCLE\nDAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PookieLavenderText,
                        lineHeight = 14.sp
                    )

                    // Circular Progress Ring
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                            // Background track
                            drawCircle(
                                color = Color.White.copy(alpha = 0.8f),
                                style = Stroke(width = 5.dp.toPx())
                            )
                            // Animated sweep progress
                            val sweep = (cycleInfo.currentCycleDay.toFloat() / cycleInfo.cycleLengthDays.toFloat()) * 360f
                            drawArc(
                                brush = Brush.sweepGradient(listOf(PookiePinkPrimary, PookieLavender)),
                                startAngle = -90f,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "${cycleInfo.currentCycleDay}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PookieTextDark
                        )
                    }
                }
            }

            // Card 2: FERTILITY WINDOW (Soft Yellow)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PookiePastelYellow)
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Fertility Window",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PookieYellowText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${fertilityStart.format(dateFormatter)} - ${fertilityEnd.format(dateFormatter)}",
                            fontSize = 10.sp,
                            color = PookieYellowText.copy(alpha = 0.8f)
                        )
                    }

                    // Sprout Icon
                    Box(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("🌱", fontSize = 28.sp)
                    }
                }
            }

            // Card 3: OVULATION (Soft Peach)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PookiePastelPeach)
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Ovulation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PookiePeachText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = ovulationDate.format(dateFormatter),
                            fontSize = 10.sp,
                            color = PookiePeachText.copy(alpha = 0.8f)
                        )
                    }

                    // Ovulation Sparkle/Egg Icon
                    Box(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("✨🥚", fontSize = 22.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 4. "HOW ARE YOU FEELING TODAY?" CARD WITH SLEEPING SUN/MOON
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "How are you feeling today?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tell us more about your body to get analysis",
                        fontSize = 11.sp,
                        color = PookieTextMuted,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onOpenLogPeriodDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = PookieLavender),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text("Add Symptom", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Sleeping Sun & Moon illustration
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cute_moon_sun),
                        contentDescription = "Mood Mascot",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 5. HEALTH & FITNESS PROGRAMS HERO CARD
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(PookieCardBg)
                .clickable { onOpenFitness() }
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PookiePinkPrimary.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔥", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Guided Fitness & Workouts", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Weight Loss, Yoga, HIIT & Cycle Sync", fontSize = 11.sp, color = PookiePinkGlow)
                        }
                    }

                    Text("Explore →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PookiePinkPrimary)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Weight Loss 🔥", "Yoga Flow 🧘", "Cycle Sync 🌸", "HIIT Boost ⚡").forEach { prog ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PookieCardLight)
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(prog, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 6. TODAY'S VITALS SNAPSHOT
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(PookieCardBg)
                .clickable { onOpenHealthDetail() }
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PookieLavender.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = PookiePinkPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Today's Vitals Snapshot", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text("Vitals Details →", fontSize = 11.sp, color = PookiePinkPrimary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${healthSnapshot.heartRate}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PookiePinkPrimary)
                        Text("Heart Rate (bpm)", fontSize = 10.sp, color = PookieTextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${healthSnapshot.edaStress}/100", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = HealthOrange)
                        Text("Stress Score", fontSize = 10.sp, color = PookieTextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${healthSnapshot.sleepHours}h", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PookieLavender)
                        Text("Sleep", fontSize = 10.sp, color = PookieTextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${healthSnapshot.recoveryScore}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = HealthGreen)
                        Text("Recovery", fontSize = 10.sp, color = PookieTextMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 7. GYNAECOLOGISTS SPECIALISTS CARD
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .clickable { onOpenGynaecologists() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(PookiePinkPrimary, PookieLavender))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👩‍⚕️", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Consult Top Gynaecologists", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Online Video Calls & Clinic Visits", fontSize = 11.sp, color = PookieTextMuted)
                    }
                }
                Text("Book →", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PookiePinkPrimary)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 8. WOMEN SAFETY & EMERGENCY SHIELD (FAKE CALL & SOS)
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Safety & Safe Exit Shield",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "1-Tap Quick Action ⚡",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PookiePinkPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onTriggerFakeCall,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PookieCardLight),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fake Call (Bhai) 📞", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = onTriggerSos,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SOS Location 🚨", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────────────
        // 9. NYRA DAILY AI INSIGHT
        // ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PookiePinkGlow, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("NYRA Daily Health Insight", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PookiePinkGlow)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"You're in your ${cycleInfo.currentPhase.displayName}. Sleep was ${healthSnapshot.sleepHours}h with recovery score of ${healthSnapshot.recoveryScore}%. Stay hydrated and take things at your own pace today!\"",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenNyraChat,
                    colors = ButtonDefaults.buttonColors(containerColor = PookieCardLight),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Ask NYRA anything →", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
