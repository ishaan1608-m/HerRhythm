package com.herrhythm.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.CycleInfo

@Composable
fun SettingsScreen(
    cycleInfo: CycleInfo,
    onUpdateCycleSettings: (Int, Int) -> Unit
) {
    var cycleLength by remember { mutableStateOf(cycleInfo.cycleLengthDays.toFloat()) }
    var periodLength by remember { mutableStateOf(cycleInfo.periodDurationDays.toFloat()) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings & Preferences",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Customize your cycle predictions and privacy",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Average Cycle Length Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Average Cycle Length: ${cycleLength.toInt()} Days",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Slider(
                    value = cycleLength,
                    onValueChange = {
                        cycleLength = it
                        onUpdateCycleSettings(cycleLength.toInt(), periodLength.toInt())
                    },
                    valueRange = 21f..40f,
                    steps = 18
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Average Period Duration Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Period Duration: ${periodLength.toInt()} Days",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Slider(
                    value = periodLength,
                    onValueChange = {
                        periodLength = it
                        onUpdateCycleSettings(cycleLength.toInt(), periodLength.toInt())
                    },
                    valueRange = 3f..10f,
                    steps = 6
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Period Reminders", fontWeight = FontWeight.Bold)
                    Text(text = "Notify 2 days before period starts", fontSize = 12.sp, color = Color.Gray)
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
        }
    }
}
