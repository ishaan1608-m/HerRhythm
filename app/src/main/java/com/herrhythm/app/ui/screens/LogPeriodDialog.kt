@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.herrhythm.app.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.herrhythm.app.data.*
import com.herrhythm.app.ui.theme.*
import java.time.LocalDate

@Composable
fun LogPeriodDialog(
    initialDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onSaveLog: (DailyLog, Boolean) -> Unit // log, isPeriodStart
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedFlow by remember { mutableStateOf<FlowLevel?>(FlowLevel.MEDIUM) }
    var selectedMoods by remember { mutableStateOf(setOf<MoodType>(MoodType.CALM)) }
    var selectedSymptoms by remember { mutableStateOf(setOf<SymptomType>(SymptomType.CRAMPS)) }
    var waterGlasses by remember { mutableIntStateOf(6) }
    var sleepHours by remember { mutableFloatStateOf(7.5f) }
    var isPeriodStartDate by remember { mutableStateOf(true) }
    var notesText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate = LocalDate.of(year, month + 1, day)
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PookieCardBg)
                    .border(1.dp, PookieCardLight, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Title & Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🩸", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Log Period & Symptoms", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Keep your cycle tracking accurate 💕", fontSize = 12.sp, color = PookieTextMuted)
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. DATE PICKER SELECTOR
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(PookieCardLight)
                                .clickable { datePickerDialog.show() }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PookiePinkPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Log Date", fontSize = 11.sp, color = PookieTextMuted)
                                        Text("${selectedDate.dayOfMonth} ${selectedDate.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${selectedDate.year}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                Text("Change 📅", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PookiePinkPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mark as period start checkbox
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isPeriodStartDate) PookiePinkPrimary.copy(alpha = 0.2f) else PookieCardLight)
                                .clickable { isPeriodStartDate = !isPeriodStartDate }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isPeriodStartDate,
                                onCheckedChange = { isPeriodStartDate = it },
                                colors = CheckboxDefaults.colors(checkedColor = PookiePinkPrimary, uncheckedColor = PookieTextMuted)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark as Period Start Date (Updates Cycle Day 1)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. FLOW LEVEL
                        Text("Flow Intensity 🩸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FlowLevel.values().forEach { flow ->
                                val isSelected = selectedFlow == flow
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PookiePinkPrimary else PookieCardLight)
                                        .border(1.dp, if (isSelected) PookiePinkGlow else Color.Transparent, RoundedCornerShape(12.dp))
                                        .clickable { selectedFlow = if (isSelected) null else flow }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(flow.icon, fontSize = 12.sp)
                                        Text(flow.label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = Color.White)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. PHYSICAL SYMPTOMS
                        Text("Symptoms Felt Today ⚡", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SymptomType.values().forEach { symptom ->
                                val isSelected = selectedSymptoms.contains(symptom)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PookiePinkPrimary else PookieCardLight)
                                        .border(1.dp, if (isSelected) PookiePinkGlow else Color.Transparent, RoundedCornerShape(12.dp))
                                        .clickable {
                                            val next = selectedSymptoms.toMutableSet()
                                            if (isSelected) next.remove(symptom) else next.add(symptom)
                                            selectedSymptoms = next
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("${symptom.emoji} ${symptom.label}", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. MOOD & EMOTIONS
                        Text("Mood & Feelings 💭", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MoodType.values().forEach { mood ->
                                val isSelected = selectedMoods.contains(mood)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PookieLavender else PookieCardLight)
                                        .border(1.dp, if (isSelected) PookiePinkGlow else Color.Transparent, RoundedCornerShape(12.dp))
                                        .clickable {
                                            val next = selectedMoods.toMutableSet()
                                            if (isSelected) next.remove(mood) else next.add(mood)
                                            selectedMoods = next
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("${mood.emoji} ${mood.label}", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 5. HYDRATION & SLEEP
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Water
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PookieCardLight)
                                    .padding(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("💧 Water", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { waterGlasses = (waterGlasses - 1).coerceAtLeast(0) }, modifier = Modifier.size(24.dp)) {
                                            Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Text("$waterGlasses gl", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PookiePinkPrimary)
                                        IconButton(onClick = { waterGlasses = (waterGlasses + 1).coerceAtMost(20) }, modifier = Modifier.size(24.dp)) {
                                            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }

                            // Sleep
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PookieCardLight)
                                    .padding(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🌙 Sleep", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { sleepHours = (sleepHours - 0.5f).coerceAtLeast(0f) }, modifier = Modifier.size(24.dp)) {
                                            Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Text("${String.format("%.1f", sleepHours)}h", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PookieLavender)
                                        IconButton(onClick = { sleepHours = (sleepHours + 0.5f).coerceAtMost(24f) }, modifier = Modifier.size(24.dp)) {
                                            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 6. NOTES
                        Text("Personal Notes ✍️", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            placeholder = { Text("How are you feeling? Add any specific details...", fontSize = 12.sp, color = PookieTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = PookieCardLight,
                                unfocusedContainerColor = PookieCardLight,
                                focusedBorderColor = PookiePinkPrimary,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // SAVE BUTTON
                    Button(
                        onClick = {
                            val log = DailyLog(
                                date = selectedDate,
                                flow = selectedFlow,
                                moods = selectedMoods.toList(),
                                symptoms = selectedSymptoms.toList(),
                                waterGlasses = waterGlasses,
                                sleepHours = sleepHours,
                                notes = notesText
                            )
                            onSaveLog(log, isPeriodStartDate)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Save Daily Log ✨", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
