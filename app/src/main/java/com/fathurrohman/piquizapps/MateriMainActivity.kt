package com.fathurrohman.piquizapps

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MateriMainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var txtPenjelasan: TextView
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materi_main)

        playerView = findViewById(R.id.playerView)
        txtPenjelasan = findViewById(R.id.txtPenjelasan)

        val videoUrl = intent.getStringExtra("videoUrl") ?: ""
        val penjelasan = intent.getStringExtra("penjelasan") ?: ""

        txtPenjelasan.text = penjelasan

        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}