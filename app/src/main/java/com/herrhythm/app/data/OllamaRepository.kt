package com.herrhythm.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object OllamaRepository {

    private const val TAG = "OllamaRepository"
    private const val API_KEY = "66f93f2cc5cc460497fee7fd252b1e64.-zlTpKNbjdMg4NP88_cFAAHE"
    private const val BASE_URL = "https://ollama.com/api/chat"
    
    // User-Agent required by ollama.com Google Frontend (Dalvik default gets 403 Forbidden)
    private const val USER_AGENT = "ollama/0.1.32 (x86_64 windows) Go/1.22.0"
    
    // Verified available & authorized models on this Ollama key
    private val CANDIDATE_MODELS = listOf("gemma4:31b", "gpt-oss:20b", "gpt-oss:120b")
    private const val TIMEOUT_MS = 25000

    data class ChatMessage(val role: String, val content: String)

    suspend fun chat(
        systemPrompt: String,
        conversationHistory: List<ChatMessage>,
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        var lastError: Exception? = null

        for (model in CANDIDATE_MODELS) {
            val result = executeChatRequest(model, systemPrompt, conversationHistory, userMessage)
            if (result.isSuccess) {
                return@withContext result
            } else {
                Log.w(TAG, "Model $model attempt failed: ${result.exceptionOrNull()?.message}")
                lastError = result.exceptionOrNull() as? Exception
            }
        }

        Result.failure(lastError ?: Exception("All Ollama candidate models failed"))
    }

    private fun executeChatRequest(
        modelName: String,
        systemPrompt: String,
        conversationHistory: List<ChatMessage>,
        userMessage: String
    ): Result<String> {
        return try {
            val messagesArray = JSONArray()

            // System prompt
            messagesArray.put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })

            // Conversation history (last 8 messages for context)
            conversationHistory.takeLast(8).forEach { msg ->
                messagesArray.put(JSONObject().apply {
                    put("role", msg.role)
                    put("content", msg.content)
                })
            }

            // Current user message
            messagesArray.put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })

            val requestBody = JSONObject().apply {
                put("model", modelName)
                put("messages", messagesArray)
                put("stream", false)
            }.toString()

            val url = URL(BASE_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Authorization", "Bearer $API_KEY")
                setRequestProperty("Accept", "application/json")
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                doOutput = true
                doInput = true
                useCaches = false
            }

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(requestBody)
                writer.flush()
            }

            val responseCode = connection.responseCode
            Log.d(TAG, "Ollama response code for $modelName: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = BufferedReader(InputStreamReader(connection.inputStream, Charsets.UTF_8)).use {
                    it.readText()
                }
                val json = JSONObject(response)
                var rawContent = json
                    .getJSONObject("message")
                    .getString("content")
                    .trim()

                // Clean any <think> tags if emitted
                if (rawContent.contains("<think>") && rawContent.contains("</think>")) {
                    rawContent = rawContent.replace(Regex("<think>[\\s\\S]*?</think>"), "").trim()
                }

                Result.success(rawContent)
            } else {
                val errorStream = connection.errorStream ?: connection.inputStream
                val errorResponse = try {
                    BufferedReader(InputStreamReader(errorStream, Charsets.UTF_8)).use { it.readText() }
                } catch (e: Exception) { "HTTP $responseCode" }
                Log.e(TAG, "Ollama API Error $responseCode for $modelName: $errorResponse")
                Result.failure(Exception("Ollama error ($responseCode): $errorResponse"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network exception calling Ollama ($modelName)", e)
            Result.failure(e)
        }
    }
}
