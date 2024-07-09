package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.instruments.App
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
        switch.isChecked = (applicationContext as App).darkTheme
        switch.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }

        val shareApp = findViewById<TextView>(R.id.share_app)
        shareApp.setOnClickListener {
            sendMessage(getString(R.string.url_practicum))
        }

        val writeInSupport = findViewById<TextView>(R.id.write_in_support)
        writeInSupport.setOnClickListener {
            sendMail(
                getString(R.string.mail),
                getString(R.string.message_to_support),
                getString(R.string.subject_of_message_to_support)
            )
        }

        val userAgreement = findViewById<TextView>(R.id.user_agreement)
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