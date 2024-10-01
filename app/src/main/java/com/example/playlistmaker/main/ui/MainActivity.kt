package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.media.ui.MediaActivity
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonSearch.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.buttonMedia.setOnClickListener {
            val intent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSettings.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}