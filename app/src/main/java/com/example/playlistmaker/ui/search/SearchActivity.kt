package com.example.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.common.constants.SearchErrors
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val KEY = "KEY"
        const val DEFAULT_VALUE = ""
        const val INTENT_KEY = "TRACK"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val MAX_SIZES_SEARCH_HISTORY = 10
    }

    private var isCLickAllowed = true

    private var userInput: String = DEFAULT_VALUE

    private val searchRunnable = Runnable {
        loadTracks()
    }

    private lateinit var binding: ActivitySearchBinding

    private lateinit var searchInteractor: SearchInteractor

    private val tracks = ArrayList<Track>()
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())

        binding.iconBack.setOnClickListener {
            finish()
        }

        searchInteractor = Creator.provideSearchInteractor()

        val onItemClickListener = OnItemClickListener { item ->
            if (clickDebounce()) {
                addTrackToHistory(item)
                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra(INTENT_KEY, item)
                }
                startActivity(intent)
            }
        }

        searchHistoryAdapter = TrackAdapter(onItemClickListener)
        searchHistoryAdapter.tracks =
            searchInteractor.gerFromSharedPreferences().toMutableList() as ArrayList<Track>

        searchAdapter = TrackAdapter(onItemClickListener)
        searchAdapter.tracks = tracks
        binding.tracksList.adapter = searchAdapter
        binding.tracksList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.iconClearSearch.visibility = setGoneIfEmpty(s)
                binding.llTracksList.visibility = setVisibleIfEmpty(s)
                binding.groupForError.visibility = setVisibleIfEmpty(s)
                binding.progressBar.visibility = setGoneIfEmpty(s)
                showHistory(binding.editTextSearch.hasFocus() && s?.isEmpty() == true)
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                userInput = s.toString()
            }
        }
        binding.editTextSearch.addTextChangedListener(textWatcher)

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            showHistory(hasFocus && binding.editTextSearch.text.isEmpty())
        }

        binding.reloadButton.setOnClickListener {
            binding.groupForError.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            searchDebounce()
        }

        binding.iconClearSearch.setOnClickListener {
            binding.editTextSearch.setText("")
            tracks.clear()
            searchAdapter.notifyDataSetChanged()
            showMessage(SearchErrors.NO_ERRORS)
        }

        binding.clearHistoryButton.setOnClickListener {
            searchInteractor.clearSharedPreferences()
            searchHistoryAdapter.tracks.clear()
            searchHistoryAdapter.notifyDataSetChanged()
            showHistory(false)
        }
    }

    private fun addTrackToHistory(item: Track) {
        val position = checkAndRemoveItem(item)
        if (position != null) searchHistoryAdapter.notifyItemRemoved(position)
        if (searchHistoryAdapter.tracks.size == MAX_SIZES_SEARCH_HISTORY) {
            searchHistoryAdapter.tracks.removeLast()
            searchHistoryAdapter.notifyItemRemoved(MAX_SIZES_SEARCH_HISTORY - 1)
        }
        searchHistoryAdapter.tracks.add(0, item)
        searchHistoryAdapter.notifyItemInserted(0)
        searchInteractor.saveInSharedPreferences(searchHistoryAdapter.tracks)
    }

    private fun checkAndRemoveItem(track: Track): Int? {
        val index = searchHistoryAdapter.tracks.indexOf(track)
        return if (index != -1) {
            searchHistoryAdapter.tracks.remove(track)
            index
        } else {
            null
        }
    }

    private fun isEmptyHistory(): Boolean {
        val tracks = searchInteractor.gerFromSharedPreferences()
        return tracks.isEmpty()
    }

    private fun showHistory(hasFocus: Boolean) {
        if (hasFocus && !isEmptyHistory()) {
            binding.tracksList.adapter = searchHistoryAdapter
            binding.historyTittle.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.VISIBLE
            binding.llTracksList.visibility = View.VISIBLE
        } else {
            binding.tracksList.adapter = searchAdapter
            binding.historyTittle.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
        }
    }

    private fun loadTracks() {
        if (binding.editTextSearch.text.isNotEmpty()) {
            searchInteractor.searchTracks(
                binding.editTextSearch.text.toString(),
                object : SearchInteractor.TrackConsumer {
                    override fun consume(foundTracks: Pair<Int, List<Track>>) {
                        handler.post {
                            binding.progressBar.visibility = View.GONE
                            if (foundTracks.first == 200) {
                                tracks.clear()
                                if (foundTracks.second.isNotEmpty()) {
                                    binding.llTracksList.visibility = View.VISIBLE
                                    tracks.addAll(foundTracks.second)
                                    searchAdapter.notifyDataSetChanged()
                                    showMessage(SearchErrors.NO_ERRORS)
                                } else {
                                    tracks.clear()
                                    searchAdapter.notifyDataSetChanged()
                                    showMessage(SearchErrors.NOT_FOUND_ERROR)
                                }
                            } else {
                                tracks.clear()
                                searchAdapter.notifyDataSetChanged()
                                showMessage(SearchErrors.NETWORK_ERROR)
                            }
                        }
                    }
                })
        }
    }

    private fun showMessage(tag: SearchErrors) {
        when (tag) {
            SearchErrors.NOT_FOUND_ERROR -> {
                with(binding) {
                    errorImage.setImageResource(R.drawable.ic_bad_search)
                    errorTittle.setText(R.string.not_found)

                    groupForError.visibility = View.VISIBLE
                    errorImage.visibility = View.VISIBLE
                    errorTittle.visibility = View.VISIBLE
                    errorSubtittle.visibility = View.GONE
                    reloadButton.visibility = View.GONE
                }
            }

            SearchErrors.NETWORK_ERROR -> {
                with(binding) {
                    errorImage.setImageResource(R.drawable.ic_bad_connection)
                    errorTittle.setText(R.string.connection_problem)
                    errorSubtittle.setText(R.string.connection_problem_additional)

                    groupForError.visibility = View.VISIBLE
                    errorImage.visibility = View.VISIBLE
                    errorTittle.visibility = View.VISIBLE
                    errorSubtittle.visibility = View.VISIBLE
                    reloadButton.visibility = View.VISIBLE
                }
            }

            SearchErrors.NO_ERRORS -> {
                binding.groupForError.visibility = View.GONE
            }
        }
    }

    private fun setGoneIfEmpty(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setVisibleIfEmpty(s: CharSequence?): Int {
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
            binding.editTextSearch.setText(userInput)
        }
    }

    override fun onStop() {
        super.onStop()
        searchInteractor.saveInSharedPreferences(searchHistoryAdapter.tracks)
    }
}