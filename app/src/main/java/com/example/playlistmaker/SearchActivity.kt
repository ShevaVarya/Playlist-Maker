package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.SearchErrors
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.instruments.SearchHistory
import com.example.playlistmaker.trackApi.ITunesApi
import com.example.playlistmaker.trackApi.TrackResponse
import com.example.playlistmaker.trackRecyclerView.OnItemClickListener
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
        const val SHARED_PREFERENCES_NAME_FILE = "shared_preferences_history_search"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isCLickAllowed = true

    private var userInput: String = DEFAULT_VALUE

    private val searchRunnable = Runnable { makeRequest() }

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(ITunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory

    private lateinit var groupOfTracksList: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonBack: ImageView
    private lateinit var clearButtonEditText: ImageView
    private lateinit var editText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var viewGroupForError: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorTittle: TextView
    private lateinit var errorSubTittle: TextView
    private lateinit var reloadButton: Button
    private lateinit var historyTittle: TextView
    private lateinit var clearHistoryButton: Button

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        groupOfTracksList = findViewById(R.id.ll_tracks_list)
        recyclerView = findViewById<RecyclerView>(R.id.tracks_list)
        buttonBack = findViewById<ImageView>(R.id.icon_back)
        clearButtonEditText = findViewById<ImageView>(R.id.icon_clear_search)
        editText = findViewById(R.id.edit_text_search)
        progressBar = findViewById(R.id.progress_bar)
        viewGroupForError = findViewById<LinearLayout>(R.id.group_for_error)
        errorImage = findViewById<ImageView>(R.id.error_image)
        errorTittle = findViewById<TextView>(R.id.error_tittle)
        errorSubTittle = findViewById<TextView>(R.id.error_subtittle)
        reloadButton = findViewById<Button>(R.id.reload_button)
        historyTittle = findViewById(R.id.history_tittle)
        clearHistoryButton = findViewById(R.id.clear_history_button)

        handler = Handler(Looper.getMainLooper())

        buttonBack.setOnClickListener {
            finish()
        }

        val onItemClickListener = OnItemClickListener { item ->
            if (clickDebounce()) {
                searchHistory.addTrackToSearchHistory(item)
                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra("TRACK", item)
                }
                startActivity(intent)
            }
        }

        searchHistoryAdapter = TrackAdapter(onItemClickListener)
        searchHistoryAdapter.tracks = tracks
        searchHistory = SearchHistory(
            getSharedPreferences(SHARED_PREFERENCES_NAME_FILE, MODE_PRIVATE),
            searchHistoryAdapter
        )

        searchAdapter = TrackAdapter(onItemClickListener)
        searchAdapter.tracks = tracks
        recyclerView.adapter = searchAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButtonEditText.visibility = clearButtonVisibility(s)
                groupOfTracksList.visibility = View.GONE
                viewGroupForError.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                showHistory(editText.hasFocus() && s?.isEmpty() == true)
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                userInput = s.toString()
            }
        }
        editText.addTextChangedListener(textWatcher)

        editText.setOnFocusChangeListener { _, hasFocus ->
            showHistory(hasFocus && editText.text.isEmpty())
        }

        reloadButton.setOnClickListener {
            viewGroupForError.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            searchDebounce()
        }

        clearButtonEditText.setOnClickListener {
            editText.setText("")
            tracks.clear()
            searchAdapter.notifyDataSetChanged()
            showMessage(SearchErrors.NO_ERRORS)
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearSearchHistory()
            showHistory(false)
        }


    }

    private fun showHistory(hasFocus: Boolean) {
        if (hasFocus && !searchHistory.isEmptyHistory()) {
            recyclerView.adapter = searchHistoryAdapter
            historyTittle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        } else {
            recyclerView.adapter = searchAdapter
            historyTittle.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }
    }

    private fun makeRequest() {
        if (editText.text.isNotEmpty()) {

            service.searchTrack(editText.text.toString()).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            groupOfTracksList.visibility = View.VISIBLE
                            tracks.addAll(response.body()?.results!!)
                            searchAdapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            tracks.clear()
                            searchAdapter.notifyDataSetChanged()
                            showMessage(SearchErrors.NOT_FOUND_ERROR)
                        } else {
                            showMessage(SearchErrors.NO_ERRORS)
                        }
                    } else {
                        tracks.clear()
                        searchAdapter.notifyDataSetChanged()
                        showMessage(SearchErrors.NETWORK_ERROR)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    tracks.clear()
                    searchAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                    showMessage(SearchErrors.NETWORK_ERROR)
                }

            })
        }
    }

    private fun showMessage(tag: SearchErrors) {
        when (tag) {
            SearchErrors.NOT_FOUND_ERROR -> {
                errorImage.setImageResource(R.drawable.ic_bad_search)
                errorTittle.setText(R.string.not_found)

                viewGroupForError.visibility = View.VISIBLE
                errorImage.visibility = View.VISIBLE
                errorTittle.visibility = View.VISIBLE
                errorSubTittle.visibility = View.GONE
                reloadButton.visibility = View.GONE
            }

            SearchErrors.NETWORK_ERROR -> {
                errorImage.setImageResource(R.drawable.ic_bad_connection)
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isCLickAllowed
        if (isCLickAllowed) {
            isCLickAllowed = false
            handler.postDelayed({ isCLickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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

    override fun onStop() {
        super.onStop()
        searchHistory.saveTracks()
    }

}