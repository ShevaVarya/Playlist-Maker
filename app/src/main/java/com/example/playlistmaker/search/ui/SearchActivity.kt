package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val KEY = "KEY"
        const val DEFAULT_VALUE = ""
        const val INTENT_KEY = "TRACK"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val MAX_SIZES_SEARCH_HISTORY = 10
    }

    private var userInput: String = DEFAULT_VALUE

    private lateinit var binding: ActivitySearchBinding

    private lateinit var searchInteractor: SearchInteractor

    private val tracks = ArrayList<Track>()
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.iconBack.setOnClickListener {
            finish()
        }

        searchInteractor = Creator.provideSearchInteractor()

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(searchInteractor)
        )[SearchViewModel::class.java]

        viewModel.getSearchState().observe(this) {
            render(it)
        }

        val onItemClickListener = OnItemClickListener { item ->
            if (viewModel.clickDebounce()) {
                addTrackToHistory(item)
                val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra(INTENT_KEY, createJson(item))
                }
                startActivity(intent)
            }
        }

        searchHistoryAdapter = TrackAdapter(onItemClickListener)

        searchAdapter = TrackAdapter(onItemClickListener)

        binding.tracksList.adapter = searchAdapter
        binding.tracksList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.editTextSearch.hasFocus() && s?.isEmpty() == true && !viewModel.isEmptyHistory()) {
                    render(
                        SearchState.ContentHistory(
                            searchInteractor.gerFromSharedPreferences()
                                .toMutableList() as ArrayList<Track>
                        )
                    )
                }
                if (s?.isEmpty() == false) {
                    render(
                        SearchState.Loading
                    )
                     viewModel.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                userInput = s.toString()
            }
        }
        binding.editTextSearch.addTextChangedListener(textWatcher)

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.editTextSearch.text.isEmpty() && !viewModel.isEmptyHistory()) {
                render(
                    SearchState.ContentHistory(
                        searchInteractor.gerFromSharedPreferences()
                            .toMutableList() as ArrayList<Track>
                    )
                )
            } else {
                render(SearchState.ContentSearch(tracks))
            }
        }

        binding.reloadButton.setOnClickListener {
            viewModel.searchDebounce(binding.editTextSearch.text.toString())
        }

        binding.iconClearSearch.setOnClickListener {
            binding.editTextSearch.setText("")
        }

        binding.clearHistoryButton.setOnClickListener {
            render(
                SearchState.EmptyHistory
            )
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.ContentHistory -> showContentHistory(state.tracks)
            is SearchState.ContentSearch -> showContentSearch(state.tracks)
            is SearchState.EmptyHistory -> showEmptyHistory()
            is SearchState.Error -> showConnectionError(
                state.errorMessageTitle,
                state.errorMessageSubtitle
            )

            is SearchState.Loading -> showLoading()
            is SearchState.NotFound -> showNotFoundError(state.message)
        }
    }

    private fun showContentSearch(tracks: List<Track>) {
        binding.tracksList.adapter = searchAdapter

        searchAdapter.tracks.clear()
        searchAdapter.tracks.addAll(tracks)
        searchAdapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.GONE

        binding.historyTittle.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
        binding.llTracksList.visibility = View.VISIBLE
        binding.tracksList.visibility = View.VISIBLE
    }

    private fun showContentHistory(tracks: List<Track>) {
        binding.tracksList.adapter = searchHistoryAdapter

        searchHistoryAdapter.tracks.clear()
        searchHistoryAdapter.tracks.addAll(tracks)
        searchHistoryAdapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.GONE
        binding.groupForError.visibility = View.GONE

        binding.historyTittle.visibility = View.VISIBLE
        binding.clearHistoryButton.visibility = View.VISIBLE
        binding.llTracksList.visibility = View.VISIBLE
        binding.tracksList.visibility = View.VISIBLE
    }

    private fun showEmptyHistory() {
        binding.progressBar.visibility = View.GONE

        binding.historyTittle.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
        binding.groupForError.visibility = View.GONE

        searchInteractor.clearSharedPreferences()
        searchHistoryAdapter.tracks.clear()
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.iconClearSearch.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.llTracksList.visibility = View.GONE
        binding.groupForError.visibility = View.GONE
    }

    private fun showConnectionError(errorMessage: String, errorMessageSubtitle: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            llTracksList.visibility = View.GONE

            errorImage.setImageResource(R.drawable.ic_bad_connection)
            errorTittle.text = errorMessage
            errorSubtittle.text = errorMessageSubtitle

            groupForError.visibility = View.VISIBLE
            errorImage.visibility = View.VISIBLE
            errorTittle.visibility = View.VISIBLE
            errorSubtittle.visibility = View.VISIBLE
            reloadButton.visibility = View.VISIBLE
        }
    }

    private fun showNotFoundError(message: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            llTracksList.visibility = View.GONE

            errorImage.setImageResource(R.drawable.ic_bad_search)
            errorTittle.text = message

            groupForError.visibility = View.VISIBLE
            errorImage.visibility = View.VISIBLE
            errorTittle.visibility = View.VISIBLE
            errorSubtittle.visibility = View.GONE
            reloadButton.visibility = View.GONE
        }
    }

    private fun createJson(item: Any): String {
        return Gson().toJson(item)
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

        viewModel.saveInSharedPreferences(searchHistoryAdapter.tracks)
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