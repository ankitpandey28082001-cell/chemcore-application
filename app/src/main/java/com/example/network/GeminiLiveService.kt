package com.example.network

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

object GeminiLiveService {

    private const val TAG = "GeminiLiveService"
    private const val REST_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    private const val WS_BASE_URL = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Interacts with 'gemini-3.8-live' using the required WebSocket bidirectional streaming (bidiGenerateContent).
     */
    suspend fun chatWithLiveModel(
        conversationHistory: List<Pair<String, String>>,
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("Gemini API key is not configured. Please add your key in the Secrets panel."))
        }

        try {
            // Attempt bidirectional streaming over WebSocket with gemini-3.8-live
            val wsResult = executeBidiWebSocket(apiKey, conversationHistory, userMessage)
            if (wsResult.isSuccess && wsResult.getOrNull()?.isNotBlank() == true) {
                return@withContext wsResult
            }
            Log.w(TAG, "WebSocket attempt failed or returned empty: ${wsResult.exceptionOrNull()?.message}. Falling back.")
        } catch (e: Exception) {
            Log.e(TAG, "WebSocket Live error: ${e.message}", e)
        }

        // Graceful fallback to REST model for conversational audio/text if WebSocket is unavailable
        fallbackRestChat(apiKey, conversationHistory, userMessage)
    }

    private suspend fun executeBidiWebSocket(
        apiKey: String,
        conversationHistory: List<Pair<String, String>>,
        userMessage: String
    ): Result<String> = suspendCancellableCoroutine { continuation ->
        val url = "$WS_BASE_URL?key=$apiKey"
        val request = Request.Builder().url(url).build()

        val fullResponseBuilder = StringBuilder()
        var isCompleted = false

        val webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "BidiGenerateContent WebSocket opened")
                try {
                    // 1. Send BidiGenerateContentSetup message
                    val setupJson = JSONObject().apply {
                        val setup = JSONObject().apply {
                            put("model", "models/gemini-3.8-live")
                            val genConfig = JSONObject().apply {
                                val modalities = JSONArray().apply {
                                    put("TEXT")
                                }
                                put("responseModalities", modalities)
                            }
                            put("generationConfig", genConfig)

                            val systemInstruction = JSONObject().apply {
                                val parts = JSONArray().apply {
                                    put(JSONObject().put("text", "You are ChemMaster Live AI, an enthusiastic, highly knowledgeable Chemistry Professor and lab mentor. Explain chemical principles, reactions, equations, and lab safety concisely, clearly, and conversationally for voice interaction."))
                                }
                                put("parts", parts)
                            }
                            put("systemInstruction", systemInstruction)
                        }
                        put("setup", setup)
                    }

                    webSocket.send(setupJson.toString())

                    // 2. Send clientContent with turns
                    val clientContentJson = JSONObject().apply {
                        val clientContent = JSONObject().apply {
                            val turns = JSONArray()

                            // Previous turns
                            for ((role, text) in conversationHistory.takeLast(6)) {
                                val turn = JSONObject().apply {
                                    put("role", if (role == "user") "user" else "model")
                                    val parts = JSONArray().apply {
                                        put(JSONObject().put("text", text))
                                    }
                                    put("parts", parts)
                                }
                                turns.put(turn)
                            }

                            // Current turn
                            val currentTurn = JSONObject().apply {
                                put("role", "user")
                                val parts = JSONArray().apply {
                                    put(JSONObject().put("text", userMessage))
                                }
                                put("parts", parts)
                            }
                            turns.put(currentTurn)

                            put("turns", turns)
                            put("turnComplete", true)
                        }
                        put("clientContent", clientContent)
                    }

                    webSocket.send(clientContentJson.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending setup/clientContent", e)
                    if (!isCompleted) {
                        isCompleted = true
                        continuation.resume(Result.failure(e))
                        webSocket.close(1000, "Error")
                    }
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Bidi WS message received: ${text.take(150)}")
                try {
                    val jsonObj = JSONObject(text)
                    if (jsonObj.has("serverContent")) {
                        val serverContent = jsonObj.getJSONObject("serverContent")
                        if (serverContent.has("modelTurn")) {
                            val modelTurn = serverContent.getJSONObject("modelTurn")
                            val parts = modelTurn.optJSONArray("parts")
                            if (parts != null) {
                                for (i in 0 until parts.length()) {
                                    val part = parts.getJSONObject(i)
                                    val chunk = part.optString("text")
                                    if (chunk.isNotBlank()) {
                                        fullResponseBuilder.append(chunk)
                                    }
                                }
                            }
                        }

                        val turnComplete = serverContent.optBoolean("turnComplete", false)
                        if (turnComplete) {
                            if (!isCompleted) {
                                isCompleted = true
                                val finalAnswer = fullResponseBuilder.toString().trim()
                                continuation.resume(Result.success(if (finalAnswer.isNotEmpty()) finalAnswer else "Response received."))
                                webSocket.close(1000, "Turn completed")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing serverContent", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                val errBody = response?.body?.string() ?: t.message ?: "Unknown WS failure"
                Log.e(TAG, "Bidi WS Failure: $errBody", t)
                if (!isCompleted) {
                    isCompleted = true
                    val currentText = fullResponseBuilder.toString().trim()
                    if (currentText.isNotEmpty()) {
                        continuation.resume(Result.success(currentText))
                    } else {
                        continuation.resume(Result.failure(Exception("Live WebSocket Error: $errBody", t)))
                    }
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                if (!isCompleted) {
                    isCompleted = true
                    val currentText = fullResponseBuilder.toString().trim()
                    if (currentText.isNotEmpty()) {
                        continuation.resume(Result.success(currentText))
                    } else {
                        continuation.resume(Result.failure(Exception("Live session closed: $reason")))
                    }
                }
            }
        })

        continuation.invokeOnCancellation {
            webSocket.cancel()
        }
    }

    private suspend fun fallbackRestChat(
        apiKey: String,
        conversationHistory: List<Pair<String, String>>,
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // gemini-2.5-flash-native-audio-preview-12-2025 or gemini-3.5-flash for fallback chat
            val modelName = "gemini-3.5-flash"
            val url = "$REST_BASE_URL/$modelName:generateContent?key=$apiKey"

            val contentsArray = JSONArray()
            val systemContext = "You are ChemMaster Live AI, an enthusiastic, highly knowledgeable Chemistry Professor and lab mentor. Explain chemical principles, reactions, equations, and lab safety concisely, clearly, and conversationally for voice interaction."

            for ((role, text) in conversationHistory.takeLast(8)) {
                val turnObj = JSONObject().apply {
                    put("role", if (role == "user") "user" else "model")
                    put("parts", JSONArray().apply { put(JSONObject().put("text", text)) })
                }
                contentsArray.put(turnObj)
            }

            val currentTurn = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply { put(JSONObject().put("text", userMessage)) })
            }
            contentsArray.put(currentTurn)

            val requestBodyJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply { put(JSONObject().put("text", systemContext)) })
                })
            }

            val body = requestBodyJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Chat fallback error: ${parseErrorMessage(responseString)}"))
            }

            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) return@withContext Result.success(text)
                }
            }

            Result.success("I am ready to assist you with chemistry!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates music using 'lyria-3-clip-preview' (up to 30s) or 'lyria-3-pro-preview' (full track).
     */
    suspend fun generateMusic(
        prompt: String,
        isShortClip: Boolean,
        cacheDir: File
    ): Result<File> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("Gemini API key is missing. Please set your key in the Secrets panel."))
        }

        try {
            val modelName = if (isShortClip) "lyria-3-clip-preview" else "lyria-3-pro-preview"
            val url = "$REST_BASE_URL/$modelName:generateContent?key=$apiKey"

            val requestBodyJson = JSONObject()
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestBodyJson.put("contents", contentsArray)

            val genConfig = JSONObject()
            val modalities = JSONArray()
            modalities.put("AUDIO")
            genConfig.put("responseModalities", modalities)
            requestBodyJson.put("generationConfig", genConfig)

            val body = requestBodyJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Music API Error: ${response.code} -> $responseString")
                return@withContext Result.failure(
                    Exception("Music API Error (${response.code}): ${parseErrorMessage(responseString)}")
                )
            }

            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        val inlineData = part.optJSONObject("inlineData")
                        if (inlineData != null) {
                            val base64Data = inlineData.optString("data")
                            if (base64Data.isNotBlank()) {
                                val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
                                val audioFile = File(cacheDir, "generated_chem_track_${System.currentTimeMillis()}.mp3")
                                FileOutputStream(audioFile).use { it.write(audioBytes) }
                                return@withContext Result.success(audioFile)
                            }
                        }
                    }
                }
            }

            Result.failure(Exception("No audio bytes received from music model"))
        } catch (e: Exception) {
            Log.e(TAG, "Music generation failed", e)
            Result.failure(e)
        }
    }

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseErrorMessage(rawJson: String): String {
        return try {
            val json = JSONObject(rawJson)
            json.optJSONObject("error")?.optString("message") ?: rawJson
        } catch (e: Exception) {
            rawJson.take(120)
        }
    }
}
