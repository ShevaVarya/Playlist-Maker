package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.button_search)
        val mediaButton = findViewById<Button>(R.id.button_media)
        val settingsButton = findViewById<Button>(R.id.button_settings)

        val searchButtonClickListener: View.OnClickListener = object: View.OnClickListener {
            override fun onClick(v: View?) {
                printToast("Экран поиска...")
            }
        }
        searchButton.setOnClickListener(searchButtonClickListener)

        mediaButton.setOnClickListener {  printToast("Экран медиатеки...") }

        settingsButton.setOnClickListener {  printToast("Экран настроек...") }
    }

    fun printToast (message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }


}