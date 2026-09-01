package com.herrhythm.app.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object PeriodCalculator {

    fun calculateCycleDetails(lastPeriodStart: LocalDate, cycleLength: Int = 28, periodLength: Int = 5): CycleInfo {
        return CycleInfo(
            lastPeriodStartDate = lastPeriodStart,
            cycleLengthDays = cycleLength,
            periodDurationDays = periodLength
        )
    }

    fun getDaysUntilNextPeriod(cycleInfo: CycleInfo): Int {
        val today = LocalDate.now()
        val nextDate = cycleInfo.nextPeriodDate
        return ChronoUnit.DAYS.between(today, nextDate).toInt().coerceAtLeast(0)
    }

    fun getChanceOfPregnancy(cycleInfo: CycleInfo, date: LocalDate): String {
        val ovulation = cycleInfo.ovulationDate
        val diff = ChronoUnit.DAYS.between(ovulation, date).toInt()
        return when {
            diff in -2..1 -> "High"
            diff in -5..3 -> "Medium"
            else -> "Low"
        }
    }
}
