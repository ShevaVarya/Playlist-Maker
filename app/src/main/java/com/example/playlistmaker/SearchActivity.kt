package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val KEY = "KEY"
        const val DEFAULT_VALUE = ""
    }

    private var userInput: String = DEFAULT_VALUE
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val buttonBack = findViewById<ImageView>(R.id.icon_back)
        buttonBack.setOnClickListener {
            finish()
        }

        editText = findViewById(R.id.edit_text_search)
        val clearButtonEditText = findViewById<ImageView>(R.id.icon_clear_search)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButtonEditText.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                userInput = s.toString()
            }
        }

        editText.addTextChangedListener(textWatcher)

        clearButtonEditText.setOnClickListener {
            editText.setText("")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, userInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userInput = savedInstanceState.getString(KEY, DEFAULT_VALUE)
        if (userInput.isNotEmpty()) {
            editText.setText(userInput)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}