package com.fathurrohman.piquizapps

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class MateriMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materi_main)

        val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youTubePlayerView)
        val txtPenjelasan = findViewById<TextView>(R.id.txtPenjelasan)

        lifecycle.addObserver(youTubePlayerView)

        // ID video YouTube yang ingin diputar
        val videoId = "bHQJWK3wOjo"
//        val videoId = getYoutubeVideoId(videoUrl)


        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                Log.d("YouTube", "Player is ready")
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })

        // Tampilkan penjelasan materi
        txtPenjelasan.text = "Penjelasan materi terkait video."
    }
}
