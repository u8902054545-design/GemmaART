package com.example.gemmaart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.view.Gravity
import android.graphics.Color

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Основной контейнер
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this).apply {
            text = "Gemma ART (Supabase)"
            textSize = 26f
            setTextColor(Color.BLACK)
            setPadding(0, 0, 0, 50)
            gravity = Gravity.CENTER
        }

        val input = EditText(this).apply {
            hint = "Введите запрос для сервера..."
            setPadding(30, 30, 30, 30)
            setBackgroundColor(Color.WHITE)
        }

        val space = Space(this).apply { 
            minimumHeight = 30 
        }

        val btn = Button(this).apply {
            text = "Отправить"
        }

        val responseView = TextView(this).apply {
            text = "Результат появится здесь"
            textSize = 16f
            setPadding(0, 60, 0, 0)
            gravity = Gravity.CENTER
        }

        layout.addView(title)
        layout.addView(input)
        layout.addView(space)
        layout.addView(btn)
        layout.addView(responseView)

        setContentView(layout)

        // Обработка клика
        btn.setOnClickListener {
            val userText = input.text.toString()
            if (userText.isNotEmpty()) {
                responseView.text = "Связь с Supabase установлена.\nОтправляю: $userText"
            } else {
                Toast.makeText(this, "Введите текст!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
