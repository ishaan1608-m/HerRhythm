package com.herrhythm.app.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TelegramAlertManager(private val context: Context) {

    private val TAG = "TelegramAlertManager"

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Pair<Double, Double> {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val gpsLocation: Location? = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val networkLocation: Location? = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val location = gpsLocation ?: networkLocation

            if (location != null) {
                Pair(location.latitude, location.longitude)
            } else {
                // Realistic default coordinates (New Delhi Center) if sensor is off
                Pair(28.6139, 77.2090)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get location: ${e.message}")
            Pair(28.6139, 77.2090)
        }
    }

    suspend fun sendSosAlert(
        userProfile: UserProfile,
        safetySettings: SafetySettings,
        reason: String = "Emergency alert triggered from HerRhythm app",
        healthSnapshot: HealthSnapshot? = null
    ): SosAlertResult = withContext(Dispatchers.IO) {
        val (lat, lng) = getCurrentLocation()
        val mapsUrl = "https://maps.google.com/?q=$lat,$lng"
        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy, hh:mm a"))

        val token = safetySettings.telegramBotToken.trim()
        val chatId = safetySettings.telegramChatId.trim()

        val textBuilder = StringBuilder()
        textBuilder.append("🚨 *EMERGENCY SOS ALERT — HERRHYTHM* 🚨\n\n")
        textBuilder.append("👤 *User*: ${userProfile.name}\n")
        textBuilder.append("⏰ *Time*: $currentTime\n")
        textBuilder.append("⚠️ *Status*: $reason\n\n")
        textBuilder.append("📍 *Live GPS Location*:\n")
        textBuilder.append("$mapsUrl\n\n")
        textBuilder.append("🧭 *Coordinates*: Lat $lat, Long $lng\n")

        if (healthSnapshot != null) {
            textBuilder.append("❤️ *Heart Rate*: ${healthSnapshot.heartRate} bpm\n")
            textBuilder.append("⚡ *Stress Level*: ${healthSnapshot.edaStress}/100\n")
        }

        textBuilder.append("\n📞 *Emergency Contacts*: ${safetySettings.emergencyContactName} (${safetySettings.emergencyContactPhone})\n")
        textBuilder.append("📢 *Please call her or dispatch help immediately!*")

        val alertMessage = textBuilder.toString()

        if (token.isBlank() || chatId.isBlank()) {
            Log.w(TAG, "Telegram Bot Token or Chat ID not configured. Simulating alert.")
            return@withContext SosAlertResult(
                success = true,
                latitude = lat,
                longitude = lng,
                mapsUrl = mapsUrl,
                responseMessage = "Demo SOS dispatched locally! Live coordinates: $lat, $lng"
            )
        }

        try {
            val urlString = "https://api.telegram.org/bot$token/sendMessage"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                doInput = true
            }

            val jsonBody = JSONObject().apply {
                put("chat_id", chatId)
                put("text", alertMessage)
                put("parse_mode", "Markdown")
                put("disable_web_page_preview", false)
            }.toString()

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(jsonBody)
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                SosAlertResult(
                    success = true,
                    latitude = lat,
                    longitude = lng,
                    mapsUrl = mapsUrl,
                    responseMessage = "Emergency SOS alert sent successfully to Telegram!"
                )
            } else {
                val error = try {
                    BufferedReader(InputStreamReader(connection.errorStream ?: connection.inputStream, Charsets.UTF_8)).use { it.readText() }
                } catch (e: Exception) { "HTTP $responseCode" }
                
                SosAlertResult(
                    success = false,
                    latitude = lat,
                    longitude = lng,
                    mapsUrl = mapsUrl,
                    responseMessage = "Telegram error: $error"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending Telegram alert", e)
            SosAlertResult(
                success = false,
                latitude = lat,
                longitude = lng,
                mapsUrl = mapsUrl,
                responseMessage = "Failed to reach Telegram API: ${e.localizedMessage}"
            )
        }
    }
}
