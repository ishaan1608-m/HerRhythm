package com.herrhythm.app.data

import java.time.LocalDate

enum class CyclePhase(val displayName: String, val description: String, val colorHex: Long) {
    MENSTRUAL("Menstrual Phase", "Period in progress. Take rest, hydrate, and nurture yourself.", 0xFFFF5277),
    FOLLICULAR("Follicular Phase", "Rising estrogen. Energy and focus are increasing!", 0xFFFF94B8),
    OVULATION("Ovulation Phase", "Peak fertility window. High energy and enthusiasm.", 0xFF00C9A7),
    LUTEAL("Luteal Phase", "Progesterone peak. Slow down, prioritize self-care.", 0xFF8A4FFF)
}

enum class FlowLevel(val label: String, val icon: String) {
    SPOTTING("Spotting", "💧"),
    LIGHT("Light", "🩸"),
    MEDIUM("Medium", "🩸🩸"),
    HEAVY("Heavy", "🩸🩸🩸")
}

enum class MoodType(val label: String, val emoji: String) {
    HAPPY("Happy", "😊"),
    ENERGETIC("Energetic", "⚡"),
    CALM("Calm", "😌"),
    ANXIOUS("Anxious", "😟"),
    MOODY("Moody", "🎭"),
    TIRED("Tired", "😴"),
    SENSITIVE("Sensitive", "🥺")
}

enum class SymptomType(val label: String, val emoji: String) {
    CRAMPS("Cramps", "⚡"),
    HEADACHE("Headache", "🤕"),
    BLOATING("Bloating", "🎈"),
    ACNE("Acne", "✨"),
    BACKACHE("Backache", "🦴"),
    BREAST_TENDERNESS("Breast Tenderness", "🌸"),
    CRAVINGS("Cravings", "🍫")
}

data class DailyLog(
    val date: LocalDate,
    val flow: FlowLevel? = null,
    val moods: List<MoodType> = emptyList(),
    val symptoms: List<SymptomType> = emptyList(),
    val waterGlasses: Int = 0,
    val sleepHours: Float = 7.5f,
    val notes: String = ""
)

data class CycleInfo(
    val lastPeriodStartDate: LocalDate,
    val cycleLengthDays: Int = 28,
    val periodDurationDays: Int = 5
) {
    val currentCycleDay: Int
        get() {
            val today = LocalDate.now()
            val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(lastPeriodStartDate, today).toInt()
            return (daysDiff % cycleLengthDays) + 1
        }

    val nextPeriodDate: LocalDate
        get() = lastPeriodStartDate.plusDays(cycleLengthDays.toLong())

    val ovulationDate: LocalDate
        get() = lastPeriodStartDate.plusDays((cycleLengthDays - 14).toLong())

    val fertileWindowStart: LocalDate
        get() = ovulationDate.minusDays(4)

    val fertileWindowEnd: LocalDate
        get() = ovulationDate.plusDays(1)

    val currentPhase: CyclePhase
        get() {
            val day = currentCycleDay
            return when {
                day <= periodDurationDays -> CyclePhase.MENSTRUAL
                day < (cycleLengthDays - 16) -> CyclePhase.FOLLICULAR
                day in (cycleLengthDays - 16)..(cycleLengthDays - 12) -> CyclePhase.OVULATION
                else -> CyclePhase.LUTEAL
            }
        }
}

data class HealthInsight(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val detailedText: String,
    val targetPhase: CyclePhase,
    val readTimeMinutes: Int
)
