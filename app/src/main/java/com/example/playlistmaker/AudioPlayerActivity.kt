package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.IntentCompat
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.data.Track
import com.example.playlistmaker.instruments.Formatter

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var trackImage: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var trackArtist: TextView
    private lateinit var addToPlaylistButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var addToFavoriteButton: ImageButton
    private lateinit var trackTime: TextView
    private lateinit var trackCollectionName: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var groupOfFieldCollection: Group

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("TRACK", Track::class.java)?.let { track = it }
        } else {
            intent.getParcelableExtra<Track>("TRACK")?.let { track = it }
        }

        backButton = findViewById(R.id.icon_back)
        trackImage = findViewById(R.id.track_image)
        trackTitle = findViewById(R.id.track_name)
        trackArtist = findViewById(R.id.track_artist)
        addToPlaylistButton = findViewById(R.id.ib_add_to_playlist)
        playButton = findViewById(R.id.ib_play)
        addToFavoriteButton = findViewById(R.id.ib_add_to_favorite)
        trackTime = findViewById(R.id.track_time)
        trackCollectionName = findViewById(R.id.collection_name)
        trackYear = findViewById(R.id.year)
        trackGenre = findViewById(R.id.genre)
        trackCountry = findViewById(R.id.country)
        groupOfFieldCollection = findViewById(R.id.group_collection)

        backButton.setOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(Formatter.getCoverArtwork(track.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(Formatter.dpToPx(2f, this)))
            .placeholder(R.drawable.placeholder)
            .into(trackImage)

        track.apply {
            trackTitle.text = trackName
            trackArtist.text = artistName
            trackTime.text = Formatter.msToMinute(trackTimeMillis)
            if (collectionName.isNullOrEmpty()) {
                groupOfFieldCollection.visibility = View.GONE
            } else {
                groupOfFieldCollection.visibility = View.VISIBLE
                trackCollectionName.text = collectionName
            }
                trackYear.text = Formatter.getYear(releaseDate)
                trackGenre.text = primaryGenreName
                trackCountry.text = country
        }
    }
}