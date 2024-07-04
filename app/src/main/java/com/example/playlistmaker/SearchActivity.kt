package com.example.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.SearchErrors
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.trackApi.ITunesApi
import com.example.playlistmaker.trackApi.TrackResponse
import com.example.playlistmaker.trackRecyclerView.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val KEY = "KEY"
        const val DEFAULT_VALUE = ""
    }

    private var userInput: String = DEFAULT_VALUE

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(ITunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private var adapter = TrackAdapter()

    private lateinit var editText: EditText
    private lateinit var viewGroupForError: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorTittle: TextView
    private lateinit var errorSubTittle: TextView
    private lateinit var reloadButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recyclerview = findViewById<RecyclerView>(R.id.tracks_list)
        val buttonBack = findViewById<ImageView>(R.id.icon_back)
        val clearButtonEditText = findViewById<ImageView>(R.id.icon_clear_search)
        editText = findViewById(R.id.edit_text_search)
        viewGroupForError = findViewById<LinearLayout>(R.id.group_for_error)
        errorImage = findViewById<ImageView>(R.id.error_image)
        errorTittle = findViewById<TextView>(R.id.error_tittle)
        errorSubTittle = findViewById<TextView>(R.id.error_subtittle)
        reloadButton = findViewById<Button>(R.id.reload_button)

        adapter.tracks = tracks
        recyclerview.adapter = adapter

        recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        buttonBack.setOnClickListener {
            finish()
        }

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

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                makeRequest()
                true
            }
            false
        }

        reloadButton.setOnClickListener {
            makeRequest()
        }

        clearButtonEditText.setOnClickListener {
            editText.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun makeRequest() {
        if (editText.text.isNotEmpty()) {
            service.searchTrack(editText.text.toString()).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            tracks.clear()
                            adapter.notifyDataSetChanged()
                            showMessage(SearchErrors.NOT_FOUND_ERROR)
                        } else {
                            showMessage(SearchErrors.NO_ERRORS)
                        }
                    } else {
                        tracks.clear()
                        adapter.notifyDataSetChanged()
                        showMessage(SearchErrors.NETWORK_ERROR)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    showMessage(SearchErrors.NETWORK_ERROR)
                }

            })
        }
    }

    private fun showMessage(tag: SearchErrors) {
        val isNightMode = isNightModeEnabled(this)
        when (tag) {
            SearchErrors.NOT_FOUND_ERROR -> {
                if (isNightMode) {
                    errorImage.setImageResource(R.drawable.ic_bad_search_dark_mode)
                } else {
                    errorImage.setImageResource(R.drawable.ic_bad_search_light_mode)
                }
                errorTittle.setText(R.string.not_found)

                viewGroupForError.visibility = View.VISIBLE
                errorImage.visibility = View.VISIBLE
                errorTittle.visibility = View.VISIBLE
                errorSubTittle.visibility = View.GONE
                reloadButton.visibility = View.GONE
            }

            SearchErrors.NETWORK_ERROR -> {
                if (isNightMode) {
                    errorImage.setImageResource(R.drawable.ic_bad_connection_dark_mode)
                } else {
                    errorImage.setImageResource(R.drawable.ic_bad_connection_light_mode)
                }
                errorTittle.setText(R.string.connection_problem)
                errorSubTittle.setText(R.string.connection_problem_additional)

                viewGroupForError.visibility = View.VISIBLE
                errorImage.visibility = View.VISIBLE
                errorTittle.visibility = View.VISIBLE
                errorSubTittle.visibility = View.VISIBLE
                reloadButton.visibility = View.VISIBLE
            }

            SearchErrors.NO_ERRORS -> {
                viewGroupForError.visibility = View.GONE
            }
        }

    }

    private fun isNightModeEnabled(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
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