package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.sharing.domain.models.IntentType

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backFromSettings.setOnClickListener {
            finish()
        }

        val sharingInteractor = Creator.provideSharingIntersctor(this@SettingsActivity)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(sharingInteractor)
        )[SettingsViewModel::class.java]

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