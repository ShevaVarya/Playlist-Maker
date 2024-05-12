package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back_from_settings)
        backButton.setOnClickListener {
            finish()
        }

        val switch = findViewById<SwitchMaterial>(R.id.switch_night_mode)
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val shareApp = findViewById<ImageView>(R.id.share_app)
        shareApp.setOnClickListener {
            sendMessage(getString(R.string.url_practicum))
        }

        val writeInSupport = findViewById<ImageView>(R.id.write_in_support)
        writeInSupport.setOnClickListener {
            sendMail(
                getString(R.string.mail),
                getString(R.string.message_to_support),
                getString(R.string.subject_of_message_to_support)
            )
        }

        val userAgreement = findViewById<ImageView>(R.id.user_agreement)
        userAgreement.setOnClickListener {
            openBrowser(getString(R.string.url_to_user_agreement))
        }

    }

    private fun sendMessage(value: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/*"
            putExtra(Intent.EXTRA_TEXT, value)
        }
        startActivity(intent)
    }

    private fun sendMail(mail: String, message: String, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(intent)
    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}