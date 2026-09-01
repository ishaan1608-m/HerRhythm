package com.herrhythm.app.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.HealthSnapshot
import com.herrhythm.app.ui.theme.*

@Composable
fun HealthScreen(
    snapshot: HealthSnapshot
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVioletBg)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Text("Health Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Real-time physiological signals & insights", fontSize = 13.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Heart Rate & HRV Section
        ExpandableHealthDetailCard(
            title = "Heart & Rhythm",
            metricValue = "${snapshot.heartRate} bpm",
            interpretation = if (snapshot.hrv > 50) "Recovery is optimal. HRV is 54ms (slightly above weekly average)." else "Elevated strain detected.",
            icon = Icons.Default.Favorite,
            accentColor = PrimaryMagenta,
            details = listOf(
                "Resting Heart Rate" to "${snapshot.restingHeartRate} bpm",
                "Heart Rate Variability (HRV)" to "${snapshot.hrv} ms",
                "ECG State" to snapshot.ecgStatus
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Blood Oxygen (SpO2)
        ExpandableHealthDetailCard(
            title = "Blood Oxygen (SpO₂)",
            metricValue = "${snapshot.spo2}%",
            interpretation = "SpO₂ remains stable within optimal range (95-100%). Healthy tissue oxygenation.",
            icon = Icons.Default.WaterDrop,
            accentColor = HealthBlue,
            details = listOf(
                "Daily Range" to "97% - 99%",
                "Sleep SpO₂ Average" to "98%",
                "Status" to "Normal & Stable"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Stress & EDA Arousal
        ExpandableHealthDetailCard(
            title = "Stress & EDA Arousal",
            metricValue = "${snapshot.edaStress} / 100",
            interpretation = if (snapshot.edaStress < 40) "Physiological arousal is low. Your nervous system is balanced." else "Elevated EDA signals detected.",
            icon = Icons.Default.SelfImprovement,
            accentColor = HealthOrange,
            details = listOf(
                "Electrodermal Activity (GSR)" to "${snapshot.edaStress} µS",
                "Autonomic Balance" to "Parasympathetic Dominant",
                "Recommendation" to "Great window for focused work or light training"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Skin Temperature
        ExpandableHealthDetailCard(
            title = "Skin Temperature Variance",
            metricValue = "${snapshot.temperature} °C",
            interpretation = "Temperature variance is +0.1°C relative to baseline, consistent with normal follicular phase.",
            icon = Icons.Default.Thermostat,
            accentColor = LightPinkGlow,
            details = listOf(
                "Nightly Deviation" to "+0.1 °C",
                "Baseline Reference" to "36.5 °C",
                "Phase Correlation" to "Follicular Phase Stable"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Sleep Analysis
        ExpandableHealthDetailCard(
            title = "Sleep & Consistency",
            metricValue = "${snapshot.sleepHours} hrs",
            interpretation = "Sleep score is ${snapshot.sleepQualityScore}/100. Deep & REM sleep cycles were well distributed.",
            icon = Icons.Default.Bedtime,
            accentColor = RadiantPurple,
            details = listOf(
                "Deep Sleep" to "1h 45m (23%)",
                "REM Sleep" to "2h 10m (28%)",
                "Light Sleep" to "3h 35m (49%)",
                "Sleep Consistency" to "92%"
            )
        )
    }
}

@Composable
fun ExpandableHealthDetailCard(
    title: String,
    metricValue: String,
    interpretation: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    details: List<Pair<String, String>>
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(metricValue, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColor)
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // What does this mean? Plain English interpretation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .padding(12.dp)
            ) {
                Text(
                    text = "💡 What this means: $interpretation",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = GlassCardBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(12.dp))

                details.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, fontSize = 13.sp, color = TextMuted)
                        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }
            }
        }
    }
}
