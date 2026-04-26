package com.example.gemmaart

import android.os.Bundle
import android.widget.*
import android.view.Gravity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
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
            setPadding(50, 50, 50, 50)
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.WHITE)
        }

        val input = EditText(this).apply {
            hint = "Напиши что-нибудь..."
            setTextColor(Color.BLACK)
        }

        val btn = Button(this).apply {
            text = "Отправить"
        }

        val responseView = TextView(this).apply {
            text = "Жду запроса..."
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, 40, 0, 0)
        }

        layout.addView(input)
        layout.addView(btn)
        layout.addView(responseView)
        setContentView(layout)

        btn.setOnClickListener {
            val prompt = input.text.toString()
            if (prompt.isNotEmpty()) {
                sendToSupabase(prompt, responseView)
            }
        }
    }

    private fun sendToSupabase(prompt: String, view: TextView) {
        runOnUiThread { view.text = "Думаю..." }
        
        val json = "{\"prompt\": \"$prompt\"}"
        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(supabaseUrl)
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("apikey", supabaseKey)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { view.text = "Ошибка: ${e.message}" }
            }
            override fun onResponse(call: Call, response: Response) {
                val resText = response.body?.string() ?: "Нет ответа"
                runOnUiThread { view.text = resText }
            }
        })
    }
}
