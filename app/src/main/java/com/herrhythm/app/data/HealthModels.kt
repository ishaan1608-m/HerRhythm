package com.herrhythm.app.data

import java.time.LocalDateTime

data class HealthSnapshot(
    val heartRate: Int = 72,          // bpm
    val restingHeartRate: Int = 62,   // bpm
    val hrv: Int = 54,                // ms
    val spo2: Int = 98,               // %
    val temperature: Float = 36.6f,   // °C
    val edaStress: Int = 28,          // 0 - 100 physiological arousal index
    val ecgStatus: String = "Normal Sinus Rhythm",
    val steps: Int = 6420,
    val caloriesBurned: Int = 380,
    val distanceKm: Float = 4.2f,
    val sleepHours: Float = 7.5f,
    val sleepQualityScore: Int = 84,   // 0 - 100
    val recoveryScore: Int = 78,       // 0 - 100
    val isWatchConnected: Boolean = true,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class UserProfile(
    val name: String = "Shivam",
    val dateOfBirth: String = "14/05/2000",
    val weightKg: Float = 55.0f,
    val occupation: String = "Designer & Tech Enthusiast",
    val activityLevel: String = "Moderately Active",
    val fitnessGoal: String = "Maintain Wellness & Energy",
    val hasWatch: Boolean = true,
    val preferredLanguage: String = "Hinglish",
    val aiPersonality: String = "Empathetic & Direct",
    val isPregnancyModeEnabled: Boolean = false,
    val conditions: List<String> = emptyList(),
    val workLifeBalance: String = "Medium",
    val relationshipStatus: String = "In a relationship",
    val lastPeriodStart: String = "12/08/2026",
    val periodDurationDays: Int = 5,
    val periodRegularity: String = "Very regular",
    val cycleLengthDays: Int = 28,
    val typicalFlow: String = "Medium",
    val reasonsToUse: List<String> = emptyList(),
    val mentalWellbeing: String = "I'm doing good 😐"
)

data class NyraInsight(
    val id: String,
    val title: String,
    val summary: String,
    val recommendation: String,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val actionType: String? = null    // WORKOUT, REMINDER, REST, SYMPTOM
)

enum class ActionType {
    CREATE_WORKOUT,
    CREATE_REMINDER,
    LOG_SYMPTOM,
    UPDATE_GOAL,
    SCHEDULE_WALK,
    TRIGGER_FAKE_CALL,
    SEND_SOS_ALERT,
    NONE
}

data class NyraActionCard(
    val title: String,
    val subtitle: String,
    val timeOrDuration: String,
    val actionType: ActionType,
    val payload: String = ""
)

data class NyraMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "NYRA"
    val text: String,
    val timestamp: String = "Just now",
    val actionCard: NyraActionCard? = null,
    val options: List<String> = emptyList()
)

data class MemoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val category: String, // "Preferences", "Routines", "People", "Goals", "Health Context"
    val content: String,
    val dateAdded: String = "Today"
)

data class WorkoutPlan(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val category: String, // Full Body, Cardio, Yoga, Recovery Walk
    val totalDurationMin: Int,
    val targetCalories: Int,
    val warmUpMin: Int = 5,
    val mainWorkoutMin: Int = 20,
    val coolDownMin: Int = 5,
    val intensity: String = "Moderate"
)

data class AchievementItem(
    val title: String,
    val description: String,
    val badgeIcon: String, // emoji or icon reference
    val isUnlocked: Boolean = false,
    val progressPercent: Float = 0.0f
)
