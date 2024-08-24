package com.example.playlistmaker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.common.util.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.implementation.IntentType

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromSettings.setOnClickListener {
            finish()
        }

        val settingsInteractor = Creator.provideSettingsInteractor()

        with(binding) {
            switchNightMode.isChecked = (applicationContext as App).darkTheme
            switchNightMode.setOnCheckedChangeListener { switcher, isChecked ->
                (applicationContext as App).switchTheme(isChecked)
            }

            shareApp.setOnClickListener {
                settingsInteractor.makeIntent(IntentType.SEND_MESSAGE, this@SettingsActivity)
            }

            writeInSupport.setOnClickListener {
                settingsInteractor.makeIntent(IntentType.SEND_EMAIL, this@SettingsActivity)
            }

            userAgreement.setOnClickListener {
                settingsInteractor.makeIntent(IntentType.OPEN_BROWSER, this@SettingsActivity)
            }
        }
    }
}