package com.herrhythm.app.data

data class SafetySettings(
    val fakeCallerName: String = "Bada Bhai ❤️",
    val fakeCallerNumber: String = "+91 98765 43210",
    val fakeCallDelaySeconds: Int = 0, // 0 = instant, 5, 10, 30
    val emergencyContactName: String = "Emergency Contact (Family)",
    val emergencyContactPhone: String = "112",
    val telegramBotToken: String = "",
    val telegramChatId: String = "",
    val isSosEnabled: Boolean = true,
    val customSosMessage: String = "I am feeling unsafe. Please check my live location and contact me immediately."
)

data class SosAlertResult(
    val success: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double,
    val mapsUrl: String,
    val responseMessage: String
)
