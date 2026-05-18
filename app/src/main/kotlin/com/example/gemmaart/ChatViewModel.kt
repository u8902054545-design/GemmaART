package com.example.gemmaart

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String,
    val isTyping: Boolean = false
)

data class Chat(
    val id: String,
    val title: String,
    val isPinned: Boolean = false
)

class ChatViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val endpoint = "https://rjdcuhxskwnchzaakjgv.supabase.co/functions/v1/gemma-node-app"
    private val anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJqZGN1aHhza3duY2h6YWFramd2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU2MjY5OTMsImV4cCI6MjA5MTIwMjk5M30.OYzreR0JnCj6JZbng9LS7DZzyqvpi8nK-vwP86rgfWE"

    val messages = mutableStateListOf<Message>()
    val chats = mutableStateListOf<Chat>()
    val input = mutableStateOf("")
    val isTyping = mutableStateOf(false)
    val selectedModel = mutableStateOf("Gemma 3 27B")
    val currentChatId = mutableStateOf(UUID.randomUUID().toString())

    val models = listOf(
        "Gemma 3 27B",
        "Gemma 3 12B",
        "Gemma 3 4B",
        "Gemini 2.0 Flash",
        "Gemini 2.0 Pro",
        "Claude 3.5 Sonnet",
        "GPT-4o"
    )

    init {
        // Mock initial chats for now
        chats.add(Chat("1", "История чата 1"))
        chats.add(Chat("2", "Обсуждение Gemma"))
    }

    fun createNewChat() {
        currentChatId.value = UUID.randomUUID().toString()
        messages.clear()
        input.value = ""
        isTyping.value = false
    }

    fun sendMessage() {
        val text = input.value.trim()
        if (text.isEmpty() || isTyping.value) return

        val userMsg = Message(role = "user", content = text)
        messages.add(userMsg)
        input.value = ""
        isTyping.value = true

        val aiMsgId = UUID.randomUUID().toString()
        val aiMsg = Message(id = aiMsgId, role = "ai", content = "", isTyping = true)
        messages.add(aiMsg)

        val json = JSONObject().apply {
            put("message", text)
            put("publicModelName", selectedModel.value)
            put("chat_id", "android_" + UUID.randomUUID().toString())
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(endpoint)
            .post(body)
            .addHeader("Authorization", "Bearer $anonKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                updateAiMessage(aiMsgId, "Ошибка: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                try {
                    val jsonResponse = JSONObject(responseData ?: "{}")
                    val content = jsonResponse.optString("response", "Пустой ответ")
                    updateAiMessage(aiMsgId, content)
                } catch (e: Exception) {
                    updateAiMessage(aiMsgId, "Ошибка парсинга: $responseData")
                }
            }
        })
    }

    private fun updateAiMessage(id: String, content: String) {
        val index = messages.indexOfFirst { it.id == id }
        if (index != -1) {
            messages[index] = messages[index].copy(content = content, isTyping = false)
        }
        isTyping.value = false
    }
}
