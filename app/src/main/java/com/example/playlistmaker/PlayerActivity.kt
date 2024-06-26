package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Track.Companion.TRACK_DATA
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private val gson = Gson()
    private val handler = Handler(Looper.getMainLooper())
    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private lateinit var buttonBack: ImageView
    private lateinit var playButton: ImageView
    private lateinit var queueButton: ImageView
    private lateinit var favoriteButton: ImageView

    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var playedTime: TextView

    private lateinit var trackTimeLabel: TextView
    private lateinit var collectionNameLabel: TextView
    private lateinit var releaseDateLabel: TextView
    private lateinit var primaryGenreNameLabel: TextView
    private lateinit var countryLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val track = intent.getSerializableExtra(TRACK_DATA) as Track
        val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
        val constraintLayout = findViewById<ConstraintLayout>(R.id.player_layout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        buttonBack = findViewById(R.id.button_back)
        playButton = findViewById(R.id.button_play)
        queueButton = findViewById(R.id.button_queue)
        favoriteButton = findViewById(R.id.button_favorite)

        albumCover = findViewById(R.id.album_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        trackTime = findViewById(R.id.track_time_data)
        collectionName = findViewById(R.id.collection_data)
        releaseDate = findViewById(R.id.year_data)
        primaryGenreName = findViewById(R.id.genre_data)
        country = findViewById(R.id.country_data)
        playedTime = findViewById(R.id.played_time)

        trackTimeLabel = findViewById(R.id.track_time_label)
        collectionNameLabel = findViewById(R.id.collection_label)
        releaseDateLabel = findViewById(R.id.year_label)
        primaryGenreNameLabel = findViewById(R.id.genre_label)
        countryLabel = findViewById(R.id.country_label)

        preparePlayer(track)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = dateFormat.format(track.trackTimeMillis)
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate.substring(0, 4)
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(applicationContext)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.placeholder_large)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(albumCover)

        if (track.collectionName.isEmpty()) {
            collectionNameLabel.text = ""
            constraintSet.connect(
                R.id.year_label,
                ConstraintSet.TOP,
                R.id.h_guideLine_8,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                R.id.genre_label,
                ConstraintSet.TOP,
                R.id.h_guideLine_9,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                R.id.country_label,
                ConstraintSet.TOP,
                R.id.h_guideLine_10,
                ConstraintSet.BOTTOM
            )
            constraintSet.applyTo(constraintLayout)
        }

        buttonBack.setOnClickListener {
            onBackPressed()
        }

        playButton.setOnClickListener {
            when (playerState) {
                STATE_PLAYING -> {
                    pausePlayer()
                }

                STATE_PREPARED, STATE_PAUSED -> {
                    startPlayer()
                }
            }
        }


    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        handler.post(updatePlayedTime())

        playButton.setImageResource(R.drawable.ic_pause)
    }

    private fun updatePlayedTime(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    playedTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                        mediaPlayer.currentPosition
                    )
                    handler.postDelayed(this, TIMER_STEP / 2)
                }
            }
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        handler.removeCallbacks(updatePlayedTime())
        playButton.setImageResource(R.drawable.ic_play)

    }

    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playedTime.setText(R.string.timer_start_time)
            playButton.setImageResource(R.drawable.ic_play)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_STEP = 1_000L
    }


}