package com.herrhythm.app.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NyraAgentEngine(
    private val memoryRepository: UserMemoryRepository,
    private val cycleRepository: CycleRepository? = null,
    private val healthRepository: HealthSensorRepository? = null,
    private val telegramAlertManager: TelegramAlertManager? = null,
    private var onTriggerFakeCall: (() -> Unit)? = null
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val TAG = "NyraAgentEngine"

    private val _messages = MutableStateFlow<List<NyraMessage>>(
        listOf(
            NyraMessage(
                sender = "NYRA",
                text = "Hey there! 🌸 I'm NYRA, your personal health & safety companion. I'm here to chat, answer cycle questions, support your workouts, or help you exit uncomfortable situations safely. How can I help you right now?",
                timestamp = getCurrentTime(),
                options = listOf("Feeling great ✨", "Log my symptoms 🩸", "📞 Safe Exit Fake Call")
            )
        )
    )
    val messages: StateFlow<List<NyraMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _activeReminders = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "Hydration" to "2:00 PM (Drink 500ml water)",
            "Evening Walk" to "5:30 PM (30 min light stroll)"
        )
    )
    val activeReminders: StateFlow<List<Pair<String, String>>> = _activeReminders.asStateFlow()

    private val _customWorkouts = MutableStateFlow<List<WorkoutPlan>>(
        listOf(
            WorkoutPlan(
                title = "30 Min Full Body Flow",
                category = "Strength & Mobility",
                totalDurationMin = 30,
                targetCalories = 220,
                intensity = "Moderate"
            )
        )
    )
    val customWorkouts: StateFlow<List<WorkoutPlan>> = _customWorkouts.asStateFlow()

    // Conversation history for Ollama context
    private val conversationHistory = mutableListOf<OllamaRepository.ChatMessage>()

    // User profile — set after onboarding
    private var userProfile: UserProfile = UserProfile()
    private var safetySettings: SafetySettings = SafetySettings()

    fun setUserProfile(profile: UserProfile) {
        userProfile = profile
        val updatedWelcome = NyraMessage(
            sender = "NYRA",
            text = "Hey ${profile.name}! 🌸 I'm NYRA, your personal health & safety companion. I know your cycle and I'm here for your health, fitness, or if you ever need a safe exit or emergency SOS. What's on your mind?",
            timestamp = getCurrentTime(),
            options = listOf("Feeling great ✨", "Log my symptoms 🩸", "📞 Safe Exit Fake Call")
        )
        _messages.value = listOf(updatedWelcome)
    }

    fun setSafetySettings(settings: SafetySettings) {
        safetySettings = settings
    }

    fun setOnTriggerFakeCall(callback: () -> Unit) {
        onTriggerFakeCall = callback
    }

    fun isEmergencyKeyword(text: String): Boolean {
        val lower = text.lowercase()
        val keywords = listOf(
            "unsafe", "emergency", "help me", "danger", "followed", "following me",
            "scared", "sos", "save me", "uncomfortable", "creep", "stalking", "call me",
            "fake call", "urgent", "trap", "threat", "bachao", "someone is following"
        )
        return keywords.any { lower.contains(it) }
    }

    fun sendMessage(userText: String) {
        if (_isLoading.value) return

        // Add user message immediately
        val userMsg = NyraMessage(sender = "USER", text = userText, timestamp = getCurrentTime())
        _messages.value = _messages.value + userMsg

        // Check for Fake Call trigger requests
        val lowerText = userText.lowercase()
        if (lowerText.contains("fake call") || lowerText.contains("call me from") || lowerText.contains("call me now")) {
            val responseText = "Calling you right away from ${safetySettings.fakeCallerName}! Answer to excuse yourself safely 🌸"
            val nyraMsg = NyraMessage(
                sender = "NYRA",
                text = responseText,
                timestamp = getCurrentTime(),
                actionCard = NyraActionCard(
                    title = "📞 Fake Call: ${safetySettings.fakeCallerName}",
                    subtitle = "Tap to trigger incoming call simulator immediately",
                    timeOrDuration = "Instant",
                    actionType = ActionType.TRIGGER_FAKE_CALL
                )
            )
            _messages.value = _messages.value + nyraMsg
            onTriggerFakeCall?.invoke()
            return
        }

        // Check for Safety / Emergency triggers
        if (isEmergencyKeyword(userText)) {
            val healthSnapshot = healthRepository?.liveSnapshot?.value

            // Dispatch Telegram SOS broadcast with live location asynchronously
            scope.launch {
                telegramAlertManager?.sendSosAlert(
                    userProfile = userProfile,
                    safetySettings = safetySettings,
                    reason = "User reported: \"$userText\"",
                    healthSnapshot = healthSnapshot
                )
            }

            val (lat, lng) = telegramAlertManager?.getCurrentLocation() ?: Pair(28.6139, 77.2090)
            val mapsUrl = "https://maps.google.com/?q=$lat,$lng"

            val emergencyResponse = "🚨 ${userProfile.name}, I am right here with you! I have immediately dispatched an Emergency SOS Alert with your live GPS location to Telegram and your emergency contact (${safetySettings.emergencyContactPhone}).\n\n📍 Live Location: $mapsUrl\n\nTap below to trigger an immediate Fake Call from ${safetySettings.fakeCallerName} so you can excuse yourself safely and move to a crowded public space."

            val emergencyCard = NyraActionCard(
                title = "📞 Safe Exit Call (${safetySettings.fakeCallerName})",
                subtitle = "Incoming call simulator to leave uncomfortable situations safely",
                timeOrDuration = "Tap to Ring",
                actionType = ActionType.TRIGGER_FAKE_CALL
            )

            val emergencyMsg = NyraMessage(
                sender = "NYRA",
                text = emergencyResponse,
                timestamp = getCurrentTime(),
                actionCard = emergencyCard,
                options = listOf("📞 Ring Phone Now", "🚨 Call Helpline 112", "I am safe now 💕")
            )
            _messages.value = _messages.value + emergencyMsg
            return
        }

        // Get context data
        val cycleInfo = cycleRepository?.getCycleInfo()
        val healthSnapshot = healthRepository?.liveSnapshot?.value ?: HealthSnapshot()
        val memories = memoryRepository.memories.value
        val recentSymptoms = memories
            .filter { it.category == "Symptom" || it.category == "NYRA Interaction" }
            .take(3)
            .map { it.content }

        // Build system prompt with user's full context
        val systemPrompt = if (cycleInfo != null) {
            NyraContextBuilder.buildSystemPrompt(
                userProfile = userProfile,
                cycleInfo = cycleInfo,
                healthSnapshot = healthSnapshot,
                memories = memories,
                recentSymptoms = recentSymptoms
            )
        } else {
            buildBasicSystemPrompt()
        }

        _isLoading.value = true

        scope.launch {
            try {
                val result = OllamaRepository.chat(
                    systemPrompt = systemPrompt,
                    conversationHistory = conversationHistory,
                    userMessage = userText
                )

                result.fold(
                    onSuccess = { reply ->
                        conversationHistory.add(OllamaRepository.ChatMessage("user", userText))
                        conversationHistory.add(OllamaRepository.ChatMessage("assistant", reply))

                        if (conversationHistory.size > 20) {
                            conversationHistory.subList(0, conversationHistory.size - 20).clear()
                        }

                        val nyraMsg = NyraMessage(
                            sender = "NYRA",
                            text = reply,
                            timestamp = getCurrentTime()
                        )
                        _messages.value = _messages.value + nyraMsg

                        if (reply.length > 30) {
                            val category = when {
                                userText.contains("pain", ignoreCase = true) ||
                                userText.contains("cramp", ignoreCase = true) ||
                                userText.contains("symptom", ignoreCase = true) -> "Symptom"
                                userText.contains("prefer", ignoreCase = true) ||
                                userText.contains("like", ignoreCase = true) ||
                                userText.contains("goal", ignoreCase = true) -> "Preference"
                                else -> "Health Note"
                            }
                            memoryRepository.addMemory(
                                category = category,
                                content = "User mentioned: $userText"
                            )
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Ollama chat request failed", error)
                        val fallbackMsg = NyraMessage(
                            sender = "NYRA",
                            text = getFallbackResponse(userText),
                            timestamp = getCurrentTime()
                        )
                        _messages.value = _messages.value + fallbackMsg
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception during sendMessage", e)
                val fallbackMsg = NyraMessage(
                    sender = "NYRA",
                    text = getFallbackResponse(userText),
                    timestamp = getCurrentTime()
                )
                _messages.value = _messages.value + fallbackMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun buildBasicSystemPrompt(): String {
        return """
You are NYRA, a warm and empathetic personal health and safety companion for women. 
You speak like a caring best friend who is also knowledgeable about women's health.
Be concise (2-4 sentences), warm, and genuinely helpful.
Use emojis sparingly. Don't mention being an AI or any underlying technology.
        """.trimIndent()
    }

    private fun getFallbackResponse(input: String): String {
        val lower = input.lowercase()
        return when {
            lower.contains("cramp") || lower.contains("pain") || lower.contains("abdomen") ->
                "That sounds uncomfortable 🌸 Try a warm compress or heating pad on your lower abdomen — it helps with cramps. Stay hydrated and rest as much as you can. If the pain is severe, please consult your doctor."
            lower.contains("headache") ->
                "Headaches can often be hormone-related, especially before or during your period 🌸 Drink plenty of water, rest in a quiet dim space, and try a gentle temple massage. It usually brings relief!"
            lower.contains("tired") || lower.contains("fatigue") ->
                "Feeling tired is completely natural 😴 Your body works hard through hormone shifts. Try to get some restful sleep, eat nutrient-dense meals, and take it easy today!"
            lower.contains("workout") || lower.contains("exercise") ->
                "I'd love to help you stay active! 💪 Check out the Fitness tab — we have Weight Loss, Yoga, and Cycle Sync routines customized for your current energy level."
            else ->
                "I'm here for you! 🌸 Whether it's health advice, cycle tracking, workouts, or safety support — feel free to ask me anything."
        }
    }

    fun executeAction(card: NyraActionCard) {
        when (card.actionType) {
            ActionType.CREATE_REMINDER -> {
                _activeReminders.value = _activeReminders.value + (card.title to card.timeOrDuration)
            }
            ActionType.CREATE_WORKOUT -> {
                _customWorkouts.value = _customWorkouts.value + WorkoutPlan(
                    title = card.title,
                    category = "NYRA Custom",
                    totalDurationMin = 30,
                    targetCalories = 210
                )
            }
            ActionType.TRIGGER_FAKE_CALL -> {
                onTriggerFakeCall?.invoke()
            }
            else -> { /* Action logged */ }
        }
    }

    private fun getCurrentTime(): String {
        val cal = java.util.Calendar.getInstance()
        val h = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val m = cal.get(java.util.Calendar.MINUTE)
        val amPm = if (h < 12) "AM" else "PM"
        val hour = if (h % 12 == 0) 12 else h % 12
        return String.format("%d:%02d %s", hour, m, amPm)
    }
}
