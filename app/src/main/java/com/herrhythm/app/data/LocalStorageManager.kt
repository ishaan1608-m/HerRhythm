package com.herrhythm.app.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class LocalStorageManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("herrhythm_user_data", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
        private const val KEY_USER_PROFILE = "key_user_profile"
        private const val KEY_CYCLE_START_DATE = "key_cycle_start_date"
        private const val KEY_CYCLE_LENGTH = "key_cycle_length"
        private const val KEY_PERIOD_LENGTH = "key_period_length"
        private const val KEY_DAILY_LOGS = "key_daily_logs"
        private const val KEY_APPOINTMENTS = "key_appointments"
        private const val KEY_MEMORIES = "key_memories"
        private const val KEY_FITNESS_PROGRESS = "key_fitness_progress"
    }

    // ─────────────────────────────────────────────
    // ONBOARDING STATUS
    // ─────────────────────────────────────────────

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    // ─────────────────────────────────────────────
    // USER PROFILE
    // ─────────────────────────────────────────────

    fun saveUserProfile(profile: UserProfile) {
        val json = JSONObject().apply {
            put("name", profile.name)
            put("dateOfBirth", profile.dateOfBirth)
            put("weightKg", profile.weightKg.toDouble())
            put("occupation", profile.occupation)
            put("activityLevel", profile.activityLevel)
            put("fitnessGoal", profile.fitnessGoal)
            put("hasWatch", profile.hasWatch)
            put("preferredLanguage", profile.preferredLanguage)
            put("aiPersonality", profile.aiPersonality)
            put("isPregnancyModeEnabled", profile.isPregnancyModeEnabled)
            put("conditions", JSONArray(profile.conditions))
            put("workLifeBalance", profile.workLifeBalance)
            put("relationshipStatus", profile.relationshipStatus)
            put("lastPeriodStart", profile.lastPeriodStart)
            put("periodDurationDays", profile.periodDurationDays)
            put("periodRegularity", profile.periodRegularity)
            put("cycleLengthDays", profile.cycleLengthDays)
            put("typicalFlow", profile.typicalFlow)
            put("reasonsToUse", JSONArray(profile.reasonsToUse))
            put("mentalWellbeing", profile.mentalWellbeing)
        }
        prefs.edit().putString(KEY_USER_PROFILE, json.toString()).apply()
    }

    fun getUserProfile(): UserProfile? {
        val jsonStr = prefs.getString(KEY_USER_PROFILE, null) ?: return null
        return try {
            val json = JSONObject(jsonStr)
            val conditionsList = mutableListOf<String>()
            val condArr = json.optJSONArray("conditions")
            if (condArr != null) {
                for (i in 0 until condArr.length()) {
                    conditionsList.add(condArr.getString(i))
                }
            }

            val reasonsList = mutableListOf<String>()
            val reasonsArr = json.optJSONArray("reasonsToUse")
            if (reasonsArr != null) {
                for (i in 0 until reasonsArr.length()) {
                    reasonsList.add(reasonsArr.getString(i))
                }
            }

            UserProfile(
                name = json.optString("name", "Beautiful"),
                dateOfBirth = json.optString("dateOfBirth", "14/05/2000"),
                weightKg = json.optDouble("weightKg", 55.0).toFloat(),
                occupation = json.optString("occupation", "Student / Professional"),
                activityLevel = json.optString("activityLevel", "Moderately Active"),
                fitnessGoal = json.optString("fitnessGoal", "Energy & Recovery"),
                hasWatch = json.optBoolean("hasWatch", true),
                preferredLanguage = json.optString("preferredLanguage", "English"),
                aiPersonality = json.optString("aiPersonality", "Empathetic & Direct"),
                isPregnancyModeEnabled = json.optBoolean("isPregnancyModeEnabled", false),
                conditions = conditionsList,
                workLifeBalance = json.optString("workLifeBalance", "Medium"),
                relationshipStatus = json.optString("relationshipStatus", "Yes"),
                lastPeriodStart = json.optString("lastPeriodStart", "12/08/2026"),
                periodDurationDays = json.optInt("periodDurationDays", 5),
                periodRegularity = json.optString("periodRegularity", "Very regular"),
                cycleLengthDays = json.optInt("cycleLengthDays", 28),
                typicalFlow = json.optString("typicalFlow", "Medium"),
                reasonsToUse = reasonsList,
                mentalWellbeing = json.optString("mentalWellbeing", "I'm doing good 😐")
            )
        } catch (e: Exception) {
            null
        }
    }

    // ─────────────────────────────────────────────
    // CYCLE SETTINGS & PERIOD LOGS
    // ─────────────────────────────────────────────

    fun saveCycleSettings(startDate: LocalDate, cycleLength: Int, periodLength: Int) {
        prefs.edit()
            .putString(KEY_CYCLE_START_DATE, startDate.toString())
            .putInt(KEY_CYCLE_LENGTH, cycleLength)
            .putInt(KEY_PERIOD_LENGTH, periodLength)
            .apply()
    }

    fun getCycleSettings(): CycleInfo {
        val dateStr = prefs.getString(KEY_CYCLE_START_DATE, null)
        val startDate = if (dateStr != null) {
            try { LocalDate.parse(dateStr) } catch (e: Exception) { LocalDate.now().minusDays(10) }
        } else {
            LocalDate.now().minusDays(10)
        }
        val cycleLength = prefs.getInt(KEY_CYCLE_LENGTH, 28)
        val periodLength = prefs.getInt(KEY_PERIOD_LENGTH, 5)

        return CycleInfo(
            lastPeriodStartDate = startDate,
            cycleLengthDays = cycleLength,
            periodDurationDays = periodLength
        )
    }

    fun saveDailyLog(log: DailyLog) {
        val existing = getAllDailyLogs().toMutableList()
        existing.removeAll { it.date == log.date }
        existing.add(0, log)

        val arr = JSONArray()
        existing.take(60).forEach { item ->
            arr.put(JSONObject().apply {
                put("date", item.date.toString())
                put("flow", item.flow?.name)
                put("moods", JSONArray(item.moods.map { it.name }))
                put("symptoms", JSONArray(item.symptoms.map { it.name }))
                put("waterGlasses", item.waterGlasses)
                put("sleepHours", item.sleepHours.toDouble())
                put("notes", item.notes)
            })
        }
        prefs.edit().putString(KEY_DAILY_LOGS, arr.toString()).apply()
    }

    fun getAllDailyLogs(): List<DailyLog> {
        val jsonStr = prefs.getString(KEY_DAILY_LOGS, null) ?: return emptyList()
        val result = mutableListOf<DailyLog>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val date = LocalDate.parse(obj.getString("date"))
                val flow = obj.optString("flow").takeIf { it.isNotBlank() }?.let {
                    try { FlowLevel.valueOf(it) } catch (e: Exception) { null }
                }

                val moods = mutableListOf<MoodType>()
                val moodsArr = obj.optJSONArray("moods")
                if (moodsArr != null) {
                    for (m in 0 until moodsArr.length()) {
                        try { moods.add(MoodType.valueOf(moodsArr.getString(m))) } catch (e: Exception) {}
                    }
                }

                val symptoms = mutableListOf<SymptomType>()
                val symArr = obj.optJSONArray("symptoms")
                if (symArr != null) {
                    for (s in 0 until symArr.length()) {
                        try { symptoms.add(SymptomType.valueOf(symArr.getString(s))) } catch (e: Exception) {}
                    }
                }

                result.add(
                    DailyLog(
                        date = date,
                        flow = flow,
                        moods = moods,
                        symptoms = symptoms,
                        waterGlasses = obj.optInt("waterGlasses", 0),
                        sleepHours = obj.optDouble("sleepHours", 7.5).toFloat(),
                        notes = obj.optString("notes", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    // ─────────────────────────────────────────────
    // DOCTOR APPOINTMENTS
    // ─────────────────────────────────────────────

    fun saveAppointments(appointments: List<DoctorAppointment>) {
        val arr = JSONArray()
        appointments.forEach { appt ->
            arr.put(JSONObject().apply {
                put("id", appt.id)
                put("doctorId", appt.doctorId)
                put("doctorName", appt.doctorName)
                put("doctorSpecialty", appt.doctorSpecialty)
                put("doctorHospital", appt.doctorHospital)
                put("date", appt.date)
                put("timeSlot", appt.timeSlot)
                put("isOnline", appt.isOnline)
                put("consultationType", appt.consultationType)
                put("patientNotes", appt.patientNotes)
                put("status", appt.status)
                put("feePaid", appt.feePaid)
            })
        }
        prefs.edit().putString(KEY_APPOINTMENTS, arr.toString()).apply()
    }

    fun getAppointments(): List<DoctorAppointment> {
        val jsonStr = prefs.getString(KEY_APPOINTMENTS, null) ?: return emptyList()
        val list = mutableListOf<DoctorAppointment>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    DoctorAppointment(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        doctorId = obj.optString("doctorId"),
                        doctorName = obj.optString("doctorName"),
                        doctorSpecialty = obj.optString("doctorSpecialty"),
                        doctorHospital = obj.optString("doctorHospital"),
                        date = obj.optString("date"),
                        timeSlot = obj.optString("timeSlot"),
                        isOnline = obj.optBoolean("isOnline", true),
                        consultationType = obj.optString("consultationType", "Online Video Call"),
                        patientNotes = obj.optString("patientNotes", ""),
                        status = obj.optString("status", "Confirmed"),
                        feePaid = obj.optString("feePaid", "₹600")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    // ─────────────────────────────────────────────
    // USER MEMORIES
    // ─────────────────────────────────────────────

    fun saveMemories(memories: List<MemoryItem>) {
        val arr = JSONArray()
        memories.forEach { mem ->
            arr.put(JSONObject().apply {
                put("id", mem.id)
                put("category", mem.category)
                put("content", mem.content)
                put("dateAdded", mem.dateAdded)
            })
        }
        prefs.edit().putString(KEY_MEMORIES, arr.toString()).apply()
    }

    fun getMemories(): List<MemoryItem>? {
        val jsonStr = prefs.getString(KEY_MEMORIES, null) ?: return null
        val list = mutableListOf<MemoryItem>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    MemoryItem(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        category = obj.optString("category", "Note"),
                        content = obj.optString("content", ""),
                        dateAdded = obj.optString("dateAdded", "Today")
                    )
                )
            }
        } catch (e: Exception) {
            return null
        }
        return list
    }

    // ─────────────────────────────────────────────
    // SAFETY & FAKE CALL SETTINGS
    // ─────────────────────────────────────────────

    fun saveSafetySettings(settings: SafetySettings) {
        val json = JSONObject().apply {
            put("fakeCallerName", settings.fakeCallerName)
            put("fakeCallerNumber", settings.fakeCallerNumber)
            put("fakeCallDelaySeconds", settings.fakeCallDelaySeconds)
            put("emergencyContactName", settings.emergencyContactName)
            put("emergencyContactPhone", settings.emergencyContactPhone)
            put("telegramBotToken", settings.telegramBotToken)
            put("telegramChatId", settings.telegramChatId)
            put("isSosEnabled", settings.isSosEnabled)
            put("customSosMessage", settings.customSosMessage)
        }
        prefs.edit().putString("key_safety_settings", json.toString()).apply()
    }

    fun getSafetySettings(): SafetySettings {
        val jsonStr = prefs.getString("key_safety_settings", null) ?: return SafetySettings()
        return try {
            val json = JSONObject(jsonStr)
            SafetySettings(
                fakeCallerName = json.optString("fakeCallerName", "Bada Bhai ❤️"),
                fakeCallerNumber = json.optString("fakeCallerNumber", "+91 98765 43210"),
                fakeCallDelaySeconds = json.optInt("fakeCallDelaySeconds", 0),
                emergencyContactName = json.optString("emergencyContactName", "Emergency Contact (Family)"),
                emergencyContactPhone = json.optString("emergencyContactPhone", "112"),
                telegramBotToken = json.optString("telegramBotToken", ""),
                telegramChatId = json.optString("telegramChatId", ""),
                isSosEnabled = json.optBoolean("isSosEnabled", true),
                customSosMessage = json.optString("customSosMessage", "I am feeling unsafe. Please check my live location and contact me immediately.")
            )
        } catch (e: Exception) {
            SafetySettings()
        }
    }
}

