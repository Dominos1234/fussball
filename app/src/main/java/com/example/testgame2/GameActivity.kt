package com.example.testgame2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val displayMetrics = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        Log.e("size", "$height, $width")
        setContentView(GameView(this))
    }
}