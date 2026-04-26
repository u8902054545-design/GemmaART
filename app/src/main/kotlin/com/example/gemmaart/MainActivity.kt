package com.example.gemmaart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.view.Gravity
import android.graphics.Color
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val supabaseUrl = "https://rjdcuhxskwnchzaakjgv.supabase.co/functions/v1/gemma-node-app"
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJqZGN1aHhza3duY2h6YWFramd2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU2MjY5OTMsImV4cCI6MjA5MTIwMjk5M30.OYzreR0JnCj6JZbng9LS7DZzyqvpi8nK-vwP86rgfWE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
            setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val input = EditText(this).apply {
            hint = "Спроси Gemma..."
        }

        val btn = Button(this).apply {
            text = "Отправить в Supabase"
        }

        val responseView = TextView(this).apply {
            text = "Ответ от сервера появится здесь"
            textSize = 16f
            setPadding(0, 50, 0, 0)
        }

        layout.addView(input)
        layout.addView(btn)
        layout.addView(responseView)
        setContentView(layout)

        btn.setOnClickListener {
            val question = input.text.toString()
            if (question.isNotEmpty()) {
                sendRequest(question, responseView)
            }
        }
    }

    private fun sendRequest(prompt: String, view: TextView) {
        view.text = "Запрос отправлен..."
        
        // Формируем JSON. Если твой сервер ждет другой формат, поправим.
        val json = """{"prompt": "$prompt"}"""
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(supabaseUrl)
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("apikey", supabaseKey)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { view.text = "Ошибка сети: ${e.message}" }
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string() ?: "Пустой ответ"
                runOnUiThread {
                    view.text = if (response.isSuccessful) bodyString else "Ошибка сервера: ${response.code}"
                }
            }
        })
    }
}
