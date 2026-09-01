package com.herrhythm.app.data

import java.time.LocalDate

class CycleRepository {
    private var userCycleInfo = CycleInfo(
        lastPeriodStartDate = LocalDate.now().minusDays(10),
        cycleLengthDays = 28,
        periodDurationDays = 5
    )

    private val dailyLogs = mutableMapOf<LocalDate, DailyLog>()

    fun getCycleInfo(): CycleInfo = userCycleInfo

    fun updateCycleSettings(startDate: LocalDate, cycleLength: Int, periodLength: Int) {
        userCycleInfo = CycleInfo(
            lastPeriodStartDate = startDate,
            cycleLengthDays = cycleLength,
            periodDurationDays = periodLength
        )
    }

    fun saveDailyLog(log: DailyLog) {
        dailyLogs[log.date] = log
    }

    fun getLogForDate(date: LocalDate): DailyLog {
        return dailyLogs[date] ?: DailyLog(date = date)
    }

    fun getArticlesForPhase(phase: CyclePhase): List<HealthInsight> {
        return listOf(
            HealthInsight(
                id = "1",
                title = "Nutrition for the ${phase.displayName}",
                category = "Nutrition",
                summary = "Discover which essential nutrients keep your body balanced during this phase.",
                detailedText = "Focus on whole foods, adequate hydration, and balanced macronutrients tailored for progesterone and estrogen fluctuation.",
                targetPhase = phase,
                readTimeMinutes = 3
            ),
            HealthInsight(
                id = "2",
                title = "Movement & Exercise Guidance",
                category = "Fitness",
                summary = "Adjust your workout intensity to match your natural hormonal rhythm.",
                detailedText = "Listen to your body rhythm. During menstruation, prefer light yoga or walking. During follicular and ovulation phases, strength training and high energy cardio feel great!",
                targetPhase = phase,
                readTimeMinutes = 4
            ),
            HealthInsight(
                id = "3",
                title = "Sleep & Mood Harmony",
                category = "Mental Wellness",
                summary = "Simple mindfulness routines to regulate cortisol and improve restful sleep.",
                detailedText = "Magnesium, warm chamomile tea, and dimming screens 1 hour before sleep helps maintain deep REM cycles.",
                targetPhase = phase,
                readTimeMinutes = 2
            )
        )
    }
}
