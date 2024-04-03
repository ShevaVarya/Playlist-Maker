package com.example.playlistmaker

import android.content.Intent
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
                val intent: Intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
        }
        searchButton.setOnClickListener(searchButtonClickListener)

        mediaButton.setOnClickListener {
            val intent: Intent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent: Intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}