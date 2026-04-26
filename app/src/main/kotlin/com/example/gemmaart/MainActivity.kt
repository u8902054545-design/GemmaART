package com.example.gemmaart

import android.app.Activity
import android.os.Bundle
import android.widget.*
import android.view.Gravity
import android.graphics.Color
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : Activity() { // Обычный Activity, не AppCompat!

    private val client = OkHttpClient()
    private val endpoint = "https://rjdcuhxskwnchzaakjgv.supabase.co/functions/v1/hyper-service"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
            setBackgroundColor(Color.WHITE)
        }

        val input = EditText(this).apply {
            hint = "Сообщение для ИИ..."
            setTextColor(Color.BLACK)
        }

        val btn = Button(this).apply {
            text = "ОТПРАВИТЬ"
        }

        val responseView = TextView(this).apply {
            text = "Здесь будет ответ"
            textSize = 18f
            setTextColor(Color.BLUE)
            setPadding(0, 50, 0, 0)
        }

        layout.addView(input)
        layout.addView(btn)
        layout.addView(responseView)
        setContentView(layout)

        btn.setOnClickListener {
            val text = input.text.toString()
            if (text.isNotEmpty()) {
                sendRequest(text, responseView)
            }
        }
    }

    private fun sendRequest(message: String, view: TextView) {
        runOnUiThread { view.text = "Gemma думает..." }

        val json = JSONObject().apply {
            put("message", message)
            put("model", "Gemma 3 27B")
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(endpoint).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { view.text = "Ошибка сети: ${e.message}" }
            }
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    try {
                        val jsonResponse = JSONObject(responseData ?: "{}")
                        view.text = jsonResponse.optString("response", "Пустой ответ")
                    } catch (e: Exception) {
                        view.text = "Сервер ответил не JSON-ом:\n$responseData"
                    }
                }
            }
        })
    }
}
