package com.herrhythm.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.CycleInfo
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    cycleInfo: CycleInfo,
    onBackClick: () -> Unit
) {
    val today = LocalDate.now()
    val daysInMonth = today.lengthOfMonth()
    val monthName = today.month.name.lowercase().replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "$monthName ${today.year}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Cycle length: ${cycleInfo.cycleLengthDays} days • Period: ${cycleInfo.periodDurationDays} days",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weekday Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Month Days Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp)
        ) {
            items(daysInMonth) { index ->
                val dayNum = index + 1
                val isToday = dayNum == today.dayOfMonth
                val isPeriod = dayNum <= cycleInfo.periodDurationDays
                val isOvulation = dayNum == (cycleInfo.cycleLengthDays - 14)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPeriod -> Color(0xFFFF5277)
                                isOvulation -> Color(0xFF00C9A7)
                                isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (isToday) 2.dp else 0.dp,
                            color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = "$dayNum",
                        fontSize = 14.sp,
                        fontWeight = if (isToday || isPeriod) FontWeight.Bold else FontWeight.Normal,
                        color = if (isPeriod || isOvulation) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Color(0xFFFF5277), label = "Period")
            LegendItem(color = Color(0xFF00C9A7), label = "Ovulation")
            LegendItem(color = Color(0xFF8A4FFF), label = "Fertile Window")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Prediction Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Next Period Prediction",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Expected to start on ${cycleInfo.nextPeriodDate}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}
