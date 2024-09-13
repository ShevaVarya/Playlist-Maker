package com.example.playlistmaker.presentation.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.data.IntentType
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromSettings.setOnClickListener {
            finish()
        }

        val settingsInteractor = Creator.provideSettingsInteractor(this@SettingsActivity)

        with(binding) {
            switchNightMode.isChecked = (applicationContext as App).darkTheme
            switchNightMode.setOnCheckedChangeListener { switcher, isChecked ->
                (applicationContext as App).switchTheme(isChecked)
            }

            shareApp.setOnClickListener {
                settingsInteractor.makeIntent(IntentType.SEND_MESSAGE)
            }

            writeInSupport.setOnClickListener {
                settingsInteractor.makeIntent(IntentType.SEND_EMAIL)
            }

            userAgreement.setOnClickListener {
                settingsInteractor.makeIntent(IntentType.OPEN_BROWSER)
            }
        }
    }
}