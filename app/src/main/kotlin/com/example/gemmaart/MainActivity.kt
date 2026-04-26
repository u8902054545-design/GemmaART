package com.example.gemmaart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.view.Gravity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val tv = TextView(this)
        tv.text = "Gemma ART\n\nПривет, Елисей!\nСкоро здесь будет ИИ."
        tv.textSize = 24f
        tv.gravity = Gravity.CENTER
        
        setContentView(tv)
    }
}
