package com.herrhythm.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.FlowLevel
import com.herrhythm.app.data.MoodType
import com.herrhythm.app.data.SymptomType

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LogSymptomsScreen(
    onSaveClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedFlow by remember { mutableStateOf<FlowLevel?>(FlowLevel.MEDIUM) }
    val selectedMoods = remember { mutableStateListOf<MoodType>(MoodType.HAPPY) }
    val selectedSymptoms = remember { mutableStateListOf<SymptomType>(SymptomType.CRAMPS) }
    var notesText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Log Today's Symptoms",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Track how you feel to improve cycle predictions",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Flow Intensity
        Text(
            text = "Menstrual Flow",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FlowLevel.values().forEach { level ->
                val isSelected = selectedFlow == level
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFlow = level },
                    label = { Text("${level.icon} ${level.label}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mood Tracker
        Text(
            text = "Mood & Emotions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MoodType.values().forEach { mood ->
                val isSelected = selectedMoods.contains(mood)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedMoods.remove(mood) else selectedMoods.add(mood)
                    },
                    label = { Text("${mood.emoji} ${mood.label}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Physical Symptoms
        Text(
            text = "Physical Symptoms",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SymptomType.values().forEach { symptom ->
                val isSelected = selectedSymptoms.contains(symptom)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedSymptoms.remove(symptom) else selectedSymptoms.add(symptom)
                    },
                    label = { Text("${symptom.emoji} ${symptom.label}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Notes Input
        OutlinedTextField(
            value = notesText,
            onValueChange = { notesText = it },
            label = { Text("Personal Notes & Observations") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Save Daily Log", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
