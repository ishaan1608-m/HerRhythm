@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.herrhythm.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.UserProfile
import com.herrhythm.app.ui.theme.*

@Composable
fun OnboardingScreen(
    onOnboardingFinished: (UserProfile) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("14/05/2000") }
    var hasWatch by remember { mutableStateOf(true) }
    var weightKg by remember { mutableFloatStateOf(55f) }
    
    // Onboarding Fields
    var selectedConditions by remember { mutableStateOf(setOf<String>()) }
    var workLifeLevel by remember { mutableStateOf("Medium") }
    var profession by remember { mutableStateOf("") }
    var relationshipStatus by remember { mutableStateOf("Yes") }
    var lastPeriodStart by remember { mutableStateOf("12/08/2026") }
    var periodDurationDays by remember { mutableIntStateOf(5) }
    var periodRegularity by remember { mutableStateOf("Very regular") }
    var cycleLengthDays by remember { mutableIntStateOf(28) }
    var typicalFlow by remember { mutableStateOf("Medium") }
    var selectedReasons by remember { mutableStateOf(setOf("Track my periods", "Understand my body better")) }
    var mentalState by remember { mutableStateOf("I'm doing good 😐") }
    var wellnessGoal by remember { mutableStateOf("Energy & Recovery") }

    val totalSteps = 12

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
    ) {
        // Ambient background glow
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-90).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(SoftRose.copy(alpha = 0.5f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Back Button and Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    IconButton(
                        onClick = { step-- },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Step dots progress bar
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalSteps) { idx ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (idx <= step) RosePrimary else PeachBorder
                                )
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = step,
                modifier = Modifier.weight(1f),
                label = "onboardingStepFlow"
            ) { targetStep ->
                when (targetStep) {
                    0 -> Step0Welcome(onNext = { step = 1 })
                    1 -> Step1Name(name = name, onNameChange = { name = it }, onNext = { step = 2 })
                    2 -> Step2Dob(dob = dob, onDobChange = { dob = it }, onNext = { step = 3 })
                    3 -> Step3WatchCheck(hasWatch = hasWatch, onHasWatchChange = { hasWatch = it }, onNext = { step = 4 })
                    4 -> Step4Weight(weight = weightKg, onWeightChange = { weightKg = it }, onNext = { step = 5 })
                    5 -> Step5HealthConditions(
                        selectedConditions = selectedConditions,
                        onConditionsChanged = { selectedConditions = it },
                        onNext = { step = 6 }
                    )
                    6 -> Step6LifestyleAndProfession(
                        workLifeLevel = workLifeLevel,
                        onWorkLifeChanged = { workLifeLevel = it },
                        profession = profession,
                        onProfessionChanged = { profession = it },
                        onNext = { step = 7 }
                    )
                    7 -> Step7RelationshipAndPrivacy(
                        relationshipStatus = relationshipStatus,
                        onRelationshipChanged = { relationshipStatus = it },
                        onNext = { step = 8 }
                    )
                    8 -> Step8CycleDetails(
                        lastPeriodStart = lastPeriodStart,
                        onLastPeriodChanged = { lastPeriodStart = it },
                        durationDays = periodDurationDays,
                        onDurationChanged = { periodDurationDays = it },
                        regularity = periodRegularity,
                        onRegularityChanged = { periodRegularity = it },
                        cycleLength = cycleLengthDays,
                        onCycleLengthChanged = { cycleLengthDays = it },
                        flow = typicalFlow,
                        onFlowChanged = { typicalFlow = it },
                        onNext = { step = 9 }
                    )
                    9 -> Step9ReasonsToUse(
                        selectedReasons = selectedReasons,
                        onReasonsChanged = { selectedReasons = it },
                        onNext = { step = 10 }
                    )
                    10 -> Step10MentalWellbeing(
                        mentalState = mentalState,
                        onMentalStateChanged = { mentalState = it },
                        onNext = { step = 11 }
                    )
                    11 -> Step11NyraIntro(
                        goal = wellnessGoal,
                        onGoalChange = { wellnessGoal = it },
                        onFinish = {
                            onOnboardingFinished(
                                UserProfile(
                                    name = if (name.isBlank()) "Beautiful" else name,
                                    dateOfBirth = dob,
                                    weightKg = weightKg,
                                    hasWatch = hasWatch,
                                    fitnessGoal = wellnessGoal,
                                    conditions = selectedConditions.toList(),
                                    workLifeBalance = workLifeLevel,
                                    occupation = if (profession.isBlank()) "Student / Professional" else profession,
                                    relationshipStatus = relationshipStatus,
                                    lastPeriodStart = lastPeriodStart,
                                    periodDurationDays = periodDurationDays,
                                    periodRegularity = periodRegularity,
                                    cycleLengthDays = cycleLengthDays,
                                    typicalFlow = typicalFlow,
                                    reasonsToUse = selectedReasons.toList(),
                                    mentalWellbeing = mentalState
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

// MODERN VECTOR ILLUSTRATION HEADER CARD
@Composable
fun GirlAvatarHeader(
    title: String,
    subtitle: String,
    speechBubbleText: String,
    iconEmoji: String = "🌸"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, PeachBorder, RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(WarmSurface, SoftRose.copy(alpha = 0.4f)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CreamCard)
                        .border(2.dp, RosePrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iconEmoji, fontSize = 28.sp)
                }
            }

            // Speech Bubble Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CreamCard)
                    .border(1.dp, RosePrimary, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = speechBubbleText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RosePrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 26.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )
    }
}

// STEP 0: WELCOME & INFORMATION SETUP INTRO
@Composable
private fun Step0Welcome(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // App Logo & Header Badge
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(RosePrimary, DustyRose)
                        )
                    )
                    .border(2.dp, SoftRose, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🌸", fontSize = 38.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("HerRhythm", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Women's Health & Cycle Companion 💕", fontSize = 14.sp, color = RosePrimary)
        }

        // Welcome Hero Card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(CreamCard)
                .border(1.dp, PeachBorder, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Text(
                "Let's Personalize Your Profile ✨",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Enter your details once to get accurate cycle predictions, PCOS care, workouts, and consult verified gynaecologists.",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryGradientButton(
                text = "Fill Information  →",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "🔒 Your health data is securely saved to your phone",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }

        // Quick feature badges
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            OnboardingBadge(icon = Icons.Default.FavoriteBorder, label = "Cycle Tracker")
            OnboardingBadge(icon = Icons.Default.Spa, label = "PCOS Support")
            OnboardingBadge(icon = Icons.Default.MedicalServices, label = "Gynaecologists")
            OnboardingBadge(icon = Icons.Default.Security, label = "100% Private")
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

// STEP 1: NAME
@Composable
private fun Step1Name(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    val isValid = name.trim().isNotBlank()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Hey there, beautiful! 🌸", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Let's get to know you better so we can personalize everything 💕", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(40.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("What's your name?", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextPrimary)
                    Text("Tell us so NYRA can call you by it 💕", fontSize = 13.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        placeholder = { Text("Enter your name (e.g. Priya, Shivam)", color = TextMuted, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosePrimary,
                            unfocusedBorderColor = PeachBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CreamBg,
                            unfocusedContainerColor = CreamBg
                        ),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RosePrimary) }
                    )

                    if (!isValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("⚠️ Please enter your name to proceed 💕", fontSize = 12.sp, color = RosePrimary)
                    }
                }
            }
        }

        PrimaryGradientButton(
            text = "Next  →",
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// STEP 2: DOB
@Composable
private fun Step2Dob(dob: String, onDobChange: (String) -> Unit, onNext: () -> Unit) {
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    val initialYear = calendar.get(java.util.Calendar.YEAR) - 24
    val initialMonth = calendar.get(java.util.Calendar.MONTH)
    val initialDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDay = String.format("%02d", selectedDay)
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                onDobChange("$formattedDay/$formattedMonth/$selectedYear")
            },
            initialYear,
            initialMonth,
            initialDay
        )
    }

    val isValid = dob.trim().isNotBlank()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("When were you born, beautiful? 🎂", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your birth date helps us personalize your health recommendations 🌸", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(40.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Your date of birth 💕", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = TextPrimary)
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Open Calendar", tint = RosePrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dob,
                        onValueChange = onDobChange,
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosePrimary,
                            unfocusedBorderColor = PeachBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CreamBg,
                            unfocusedContainerColor = CreamBg
                        ),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RosePrimary)
                            }
                        },
                        trailingIcon = {
                            Text("Change 📅", fontSize = 12.sp, color = RosePrimary, modifier = Modifier.clickable { datePickerDialog.show() }.padding(end = 12.dp))
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap to pick from calendar 📅", fontSize = 12.sp, color = TextMuted)
                }
            }
        }

        PrimaryGradientButton(
            text = "Next  →",
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// STEP 3: WATCH CHECK
@Composable
private fun Step3WatchCheck(hasWatch: Boolean, onHasWatchChange: (Boolean) -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Do you have a HerRhythm watch? ⌚", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("It unlocks deep physiological insights (HRV, EDA stress & sleep tracking) 🌸", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(28.dp))

            SelectableOptionCard(title = "Yes", subtitle = "I have my watch paired with me 💕", icon = Icons.Default.Watch, isSelected = hasWatch, onClick = { onHasWatchChange(true) })
            Spacer(modifier = Modifier.height(14.dp))
            SelectableOptionCard(title = "Not yet", subtitle = "Use simulated sensor data (Demo Mode)", icon = Icons.Default.Smartphone, isSelected = !hasWatch, onClick = { onHasWatchChange(false) })
            Spacer(modifier = Modifier.height(16.dp))
            Text("Don't worry! You can explore all features with simulated sensor data ✨", fontSize = 12.sp, color = TextMuted, textAlign = TextAlign.Center)
        }

        PrimaryGradientButton(text = "Next  →", onClick = onNext, modifier = Modifier.fillMaxWidth())
    }
}

// STEP 4: WEIGHT
@Composable
private fun Step4Weight(weight: Float, onWeightChange: (Float) -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("How much do you weigh? ⚖️", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Helps calibrate workout calorie burns and metabolic health 🌸", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(32.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Body weight (in kg) 💕", fontSize = 15.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        IconButton(
                            onClick = { onWeightChange((weight - 1).coerceAtLeast(30f)) },
                            modifier = Modifier.size(44.dp).border(1.dp, PeachBorder, CircleShape)
                        ) {
                            Text("-", fontSize = 22.sp, color = TextPrimary)
                        }

                        Text("${weight.toInt()} kg", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = RosePrimary, modifier = Modifier.padding(horizontal = 28.dp))

                        IconButton(
                            onClick = { onWeightChange((weight + 1).coerceAtMost(150f)) },
                            modifier = Modifier.size(44.dp).border(1.dp, PeachBorder, CircleShape)
                        ) {
                            Text("+", fontSize = 22.sp, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(WarmSurface).padding(10.dp), contentAlignment = Alignment.Center) {
                        Text("✨ Small steps today, stronger you tomorrow", fontSize = 12.sp, color = RosePrimary)
                    }
                }
            }
        }

        PrimaryGradientButton(text = "Next  →", onClick = onNext, modifier = Modifier.fillMaxWidth())
    }
}

// STEP 5: HEALTH CONDITIONS
@Composable
private fun Step5HealthConditions(
    selectedConditions: Set<String>,
    onConditionsChanged: (Set<String>) -> Unit,
    onNext: () -> Unit
) {
    val allConditions = listOf(
        "PCOS / PCOD", "Endometriosis", "Irregular periods", "Very painful periods", "Heavy bleeding",
        "Very light / missed periods", "PMS / PMDD", "Hormonal Imbalance", "Acne / hormonal acne", "Excess facial or body hair",
        "Thyroid problems", "Anemia / low iron", "Migraine / period headaches", "Vaginal infections", "Urinary / UTI problems",
        "Fertility concerns", "Pregnancy / pregnancy concerns", "Menstrual or pelvic pain", "Breast-related concerns", "Menopause / perimenopause",
        "Mental health / mood concerns", "Sleep problems", "None of these", "Prefer not to say"
    )

    val isValid = selectedConditions.isNotEmpty()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            GirlAvatarHeader(
                title = "Any conditions you're dealing with? 💕",
                subtitle = "Select all that apply. 🌸 You can always change this later in settings.",
                speechBubbleText = "\"We're here for you always 💕\"",
                iconEmoji = "🩺"
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allConditions) { condition ->
                    val isSelected = selectedConditions.contains(condition)
                    Box(
                        modifier = Modifier
                            .height(64.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) RosePrimary else PeachBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .background(if (isSelected) SoftRose else CreamCard)
                            .clickable {
                                val nextSet = selectedConditions.toMutableSet()
                                if (isSelected) nextSet.remove(condition) else nextSet.add(condition)
                                onConditionsChanged(nextSet)
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(condition, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center, color = if (isSelected) RosePrimary else TextPrimary, lineHeight = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isValid) {
                Text("⚠️ Please select at least 1 option or 'None of these' to continue 💕", fontSize = 11.sp, color = RosePrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Box(modifier = Modifier.padding(top = 8.dp)) {
            PrimaryGradientButton(
                text = "Next  →",
                onClick = onNext,
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// STEP 6: LIFESTYLE & PROFESSION
@Composable
private fun Step6LifestyleAndProfession(
    workLifeLevel: String,
    onWorkLifeChanged: (String) -> Unit,
    profession: String,
    onProfessionChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isValid = profession.trim().isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            GirlAvatarHeader(
                title = "Help us understand your daily routine 🌸✨",
                subtitle = "This helps us personalize your recovery and exercise plans 💕",
                speechBubbleText = "\"Let's make HerRhythm yours! 💕\"",
                iconEmoji = "👩‍💻"
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("How would you describe your daily work-life? 🌸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("Low", "Mostly relaxed routine", "☁️"),
                            Triple("Medium", "Balanced routine", "👩‍💻"),
                            Triple("High", "Very busy / hectic", "📋")
                        ).forEach { (level, desc, icon) ->
                            val isSelected = workLifeLevel == level
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(if (isSelected) 2.dp else 1.dp, if (isSelected) RosePrimary else PeachBorder, RoundedCornerShape(14.dp))
                                    .background(if (isSelected) SoftRose else CardSurface)
                                    .clickable { onWorkLifeChanged(level) }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(level, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(desc, fontSize = 9.sp, textAlign = TextAlign.Center, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("What is your profession? 🌸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = profession,
                        onValueChange = onProfessionChanged,
                        placeholder = { Text("e.g. Student, Engineer, Designer, Doctor, etc.", color = TextMuted, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosePrimary,
                            unfocusedBorderColor = PeachBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CreamBg,
                            unfocusedContainerColor = CreamBg
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (!isValid) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("⚠️ Please enter your profession to proceed 💕", fontSize = 11.sp, color = RosePrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        PrimaryGradientButton(
            text = "Next  →",
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// STEP 7: RELATIONSHIP
@Composable
private fun Step7RelationshipAndPrivacy(
    relationshipStatus: String,
    onRelationshipChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            GirlAvatarHeader(
                title = "Just a few more things, beautiful! 💕",
                subtitle = "These help us tailor fertility and wellness insights for you 🌸",
                speechBubbleText = "\"Your privacy is 100% protected 💕\"",
                iconEmoji = "💖"
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Are you in a relationship? 🌸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("Yes", "In a relationship", "💖"),
                            Triple("No", "Not in a relationship", "☁️"),
                            Triple("Prefer not to say", "Rather skip this", "❓")
                        ).forEach { (status, desc, icon) ->
                            val isSelected = relationshipStatus == status
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(if (isSelected) 2.dp else 1.dp, if (isSelected) RosePrimary else PeachBorder, RoundedCornerShape(14.dp))
                                    .background(if (isSelected) SoftRose else CardSurface)
                                    .clickable { onRelationshipChanged(status) }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(desc, fontSize = 9.sp, textAlign = TextAlign.Center, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("You can trust HerRhythm 💕", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    PrivacyFeatureRow(icon = Icons.Default.Lock, title = "Your data stays on your phone", desc = "Stored in private app memory.")
                    PrivacyFeatureRow(icon = Icons.Default.VisibilityOff, title = "Never shared with third parties", desc = "We respect your personal privacy.")
                    PrivacyFeatureRow(icon = Icons.Default.Tune, title = "Full control in settings", desc = "Update your answers anytime.")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        PrimaryGradientButton(text = "Continue ✨  →", onClick = onNext, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun PrivacyFeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(desc, fontSize = 10.sp, color = TextMuted)
        }
    }
}

// STEP 8: CYCLE DETAILS
@Composable
private fun Step8CycleDetails(
    lastPeriodStart: String,
    onLastPeriodChanged: (String) -> Unit,
    durationDays: Int,
    onDurationChanged: (Int) -> Unit,
    regularity: String,
    onRegularityChanged: (String) -> Unit,
    cycleLength: Int,
    onCycleLengthChanged: (Int) -> Unit,
    flow: String,
    onFlowChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                val formattedDay = String.format("%02d", day)
                val formattedMonth = String.format("%02d", month + 1)
                onLastPeriodChanged("$formattedDay/$formattedMonth/$year")
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            GirlAvatarHeader(
                title = "Let's get to know your cycle 💕",
                subtitle = "These details calibrate your ovulation and period dates 🌸",
                speechBubbleText = "\"Accurate tracking for your body! 💕\"",
                iconEmoji = "🩸"
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("1. When did your last period start? 🌸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = lastPeriodStart,
                        onValueChange = onLastPeriodChanged,
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosePrimary,
                            unfocusedBorderColor = PeachBorder,
                            focusedContainerColor = CreamBg,
                            unfocusedContainerColor = CreamBg
                        ),
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RosePrimary, modifier = Modifier.clickable { datePickerDialog.show() }) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("2. How long does your period usually last? 🌸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { onDurationChanged((durationDays - 1).coerceAtLeast(2)) }) { Text("-", fontSize = 20.sp, color = TextPrimary) }
                        Text("$durationDays days", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RosePrimary, modifier = Modifier.padding(horizontal = 16.dp))
                        IconButton(onClick = { onDurationChanged((durationDays + 1).coerceAtMost(10)) }) { Text("+", fontSize = 20.sp, color = TextPrimary) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("3. How regular are your periods? 🌸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Very regular", "Mostly regular", "Irregular", "Unsure").forEach { item ->
                            val isSelected = regularity == item
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(if (isSelected) 1.5.dp else 1.dp, if (isSelected) RosePrimary else PeachBorder, RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SoftRose else CardSurface)
                                    .clickable { onRegularityChanged(item) }
                                    .padding(vertical = 6.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        PrimaryGradientButton(text = "Continue ✨  →", onClick = onNext, modifier = Modifier.fillMaxWidth())
    }
}

// STEP 9: REASONS TO USE
@Composable
private fun Step9ReasonsToUse(
    selectedReasons: Set<String>,
    onReasonsChanged: (Set<String>) -> Unit,
    onNext: () -> Unit
) {
    val allReasons = listOf(
        "Track my periods" to "🌸",
        "Understand my body better" to "💕",
        "Manage PCOS / Hormones" to "🏥",
        "Improve my sleep & energy" to "🌙",
        "Guided female workouts" to "🏃",
        "Consult gynaecologists" to "👩‍⚕️",
        "Ask private health questions" to "💬"
    )

    val scrollState = rememberScrollState()
    val isValid = selectedReasons.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            GirlAvatarHeader(
                title = "What brought you to HerRhythm? 💕",
                subtitle = "Select all that apply. 🌸",
                speechBubbleText = "\"We're here for you! 💕\"",
                iconEmoji = "🎯"
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    allReasons.forEach { (reason, icon) ->
                        val isChecked = selectedReasons.contains(reason)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val nextSet = selectedReasons.toMutableSet()
                                    if (isChecked) nextSet.remove(reason) else nextSet.add(reason)
                                    onReasonsChanged(nextSet)
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(reason, fontSize = 12.sp, color = TextPrimary)
                            }
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    val nextSet = selectedReasons.toMutableSet()
                                    if (checked) nextSet.add(reason) else nextSet.remove(reason)
                                    onReasonsChanged(nextSet)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = RosePrimary)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        PrimaryGradientButton(
            text = "Next ✨  →",
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// STEP 10: MENTAL WELLBEING
@Composable
private fun Step10MentalWellbeing(
    mentalState: String,
    onMentalStateChanged: (String) -> Unit,
    onNext: () -> Unit
) {
    val options = listOf(
        Triple("I'm feeling great! ☀️", "Happy, motivated and positive.", "🌼"),
        Triple("I'm doing good 😐", "Mostly good with normal routine.", "☁️"),
        Triple("A bit stressed 😒", "Feeling overwhelmed at times.", "⛈️"),
        Triple("Feeling low / tired 😔", "Emotionally or physically drained.", "🌧️"),
        Triple("Struggling a bit 💔", "Need extra support and care.", "💖")
    )

    val scrollState = rememberScrollState()
    val isValid = mentalState.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            GirlAvatarHeader(
                title = "How are you mentally right now? 💕",
                subtitle = "Your emotional wellbeing is directly linked to your hormonal cycle 🌸",
                speechBubbleText = "\"It's okay to take a pause 💕\"",
                iconEmoji = "🧘"
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("How are you feeling these days? 🌸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    options.forEach { (optionTitle, optionDesc, emojiIcon) ->
                        val isSelected = mentalState == optionTitle
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) RosePrimary else PeachBorder, RoundedCornerShape(12.dp))
                                .background(if (isSelected) SoftRose else CardSurface)
                                .clickable { onMentalStateChanged(optionTitle) }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(emojiIcon, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(optionTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(optionDesc, fontSize = 11.sp, color = TextMuted)
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onMentalStateChanged(optionTitle) },
                                    colors = RadioButtonDefaults.colors(selectedColor = RosePrimary)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        PrimaryGradientButton(
            text = "You're all set! ✨  →",
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// STEP 11: NYRA INTRO & FINISH
@Composable
private fun Step11NyraIntro(goal: String, onGoalChange: (String) -> Unit, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape).background(Brush.linearGradient(listOf(RosePrimary, DustyRose))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Meet NYRA 👋", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your 24/7 AI health companion. What's your primary wellness focus?", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(20.dp))

            val goals = listOf(
                "Energy & Recovery" to "Balance stress, sleep better & maintain stamina",
                "Weight Loss & Fitness" to "Daily 30-min guided routines & calorie burn",
                "Cycle & Hormone Care" to "Track symptoms & hormonal phase energy",
                "Gynaecologist Support" to "Consult doctors & get verified care"
            )

            goals.forEach { (title, subtitle) ->
                SelectableOptionCard(title = title, subtitle = subtitle, icon = Icons.Default.AutoAwesome, isSelected = goal == title, onClick = { onGoalChange(title) })
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        PrimaryGradientButton(text = "Enter HerRhythm ✨", onClick = onFinish, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun OnboardingBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape).border(1.dp, PeachBorder, CircleShape).background(CreamCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center)
    }
}

@Composable
fun SelectableOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) RosePrimary else PeachBorder, shape = RoundedCornerShape(16.dp))
            .background(if (isSelected) SoftRose else CreamCard)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) RosePrimary else TextMuted, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                Text(subtitle, fontSize = 11.sp, color = TextSecondary)
            }
            if (isSelected) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PeachBorder, RoundedCornerShape(18.dp))
            .background(CreamCard)
    ) {
        content()
    }
}

@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(26.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) Brush.horizontalGradient(colors = listOf(RosePrimary, DustyRose))
                    else Brush.horizontalGradient(colors = listOf(PeachBorder, WarmSurface))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else TextMuted
            )
        }
    }
}
