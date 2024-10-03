package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.sharing.domain.models.IntentType
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }
    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backFromSettings.setOnClickListener {
            finish()
        }

        with(binding) {
            switchNightMode.isChecked = viewModel.isChecked()
            switchNightMode.setOnCheckedChangeListener { _, isChecked ->
                viewModel.switchTheme(isChecked)
            }

            shareApp.setOnClickListener {
                viewModel.makeIntent(IntentType.SEND_MESSAGE)
            }

            writeInSupport.setOnClickListener {
                viewModel.makeIntent(IntentType.SEND_EMAIL)

            }

            userAgreement.setOnClickListener {
                viewModel.makeIntent(IntentType.OPEN_BROWSER)

            }
        }
    }
}