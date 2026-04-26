package com.example.gemmaart

import android.os.Bundle
import android.widget.*
import android.view.Gravity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val endpoint = "https://rjdcuhxskwnchzaakjgv.supabase.co/functions/v1/hyper-service"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.WHITE)
        }

        val input = EditText(this).apply {
            hint = "Введите сообщение..."
            setTextColor(Color.BLACK)
            setHintTextColor(Color.GRAY)
        }

        val btn = Button(this).apply {
            text = "Спросить ИИ"
        }

        val responseView = TextView(this).apply {
            text = "Ответ появится здесь"
            textSize = 16f
            setTextColor(Color.BLACK)
            setPadding(0, 40, 0, 0)
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
            put("model", "Gemma 3 27B") // Модель по умолчанию
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
                        view.text = "Ошибка сервера: $responseData"
                    }
                }
            }
        })
    }
}
