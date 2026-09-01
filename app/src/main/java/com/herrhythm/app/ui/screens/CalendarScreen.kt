package com.herrhythm.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.herrhythm.app.ui.theme.*
import java.time.LocalDate

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
            .background(PookieDarkBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PookieCardBg)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "$monthName ${today.year}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Cycle: ${cycleInfo.cycleLengthDays} days • Period: ${cycleInfo.periodDurationDays} days",
                    fontSize = 12.sp,
                    color = PookieTextMuted
                )
            }
        }

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
                    color = PookieTextMuted
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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPeriod -> PookiePinkPrimary
                                isOvulation -> PookieLavender
                                isToday -> PookieCardLight
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (isToday) 2.dp else 0.dp,
                            color = if (isToday) PookiePinkGlow else Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = "$dayNum",
                        fontSize = 14.sp,
                        fontWeight = if (isToday || isPeriod) FontWeight.Bold else FontWeight.Normal,
                        color = Color.White
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
            LegendItem(color = PookiePinkPrimary, label = "Period")
            LegendItem(color = PookieLavender, label = "Ovulation")
            LegendItem(color = PookiePastelYellow, label = "Fertile Window")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Prediction Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PookieCardBg)
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "Next Period Prediction 🌸",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Expected to start on ${cycleInfo.nextPeriodDate} (Day 1 of next cycle)",
                    fontSize = 13.sp,
                    color = PookiePinkGlow,
                    fontWeight = FontWeight.SemiBold
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
        Text(text = label, fontSize = 12.sp, color = PookieTextMuted)
    }
}
