package com.example.firstproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    fun addShortToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addShortToast("onCreate() Toast!")
    }
    override fun onStart() {
        super.onStart()
        addShortToast("onStart() Toast!")
    }
    override fun onResume() {
        super.onResume()
        addShortToast("onResume() Toast!")
    }
    override fun onPause() {
        super.onPause()
        addShortToast("onPause() Toast!")
    }
    override fun onStop() {
        super.onStop()
        addShortToast("onStop() Toast!")
    }
    override fun onRestart() {
        super.onRestart()
        addShortToast("onRestart() Toast!")
    }
    override fun onDestroy() {
        super.onDestroy()

        addShortToast("onDestroy() Toast!")
    }
}