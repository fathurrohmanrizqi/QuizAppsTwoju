package com.fathurrohman.piquizapps

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController

class MateriMainActivity : AppCompatActivity() {

    private var currentSecond = 0f
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var txtPenjelasan: TextView
    private lateinit var youTubePlayerView: YouTubePlayerView
    private lateinit var btnForward: ImageButton
    private lateinit var btnRewind: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materi_main)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val videoUrl = intent.getStringExtra("videoUrl") ?: ""

        youTubePlayerView = findViewById(R.id.youTubePlayerView)
        txtPenjelasan = findViewById(R.id.txtPenjelasan)
        btnForward = findViewById(R.id.btnForward)
        btnRewind = findViewById(R.id.btnRewind)

        btnForward.alpha = 1f
        btnRewind.alpha = 1f

        lifecycle.addObserver(youTubePlayerView)


        val videoId = getYoutubeVideoId(videoUrl)

        // Ambil data penjelasan dari Firestore berdasarkan videoId
        getDataFromFirebase(videoId)

        btnBack.setOnClickListener {
            finish()
        }

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                val uiController = DefaultPlayerUiController(youTubePlayerView, youTubePlayer)
                youTubePlayerView.setCustomPlayerUi(uiController.rootView)

                uiController.showFullscreenButton(true)
                uiController.setFullScreenButtonClickListener {
                    if (youTubePlayerView.isFullScreen()) {
                        youTubePlayerView.exitFullScreen()
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    } else {
                        youTubePlayerView.enterFullScreen()
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }

                fun hideControls() {
                    btnForward.animate().alpha(0f).setDuration(300).start()
                    btnRewind.animate().alpha(0f).setDuration(300).start()
                }

                fun showControls() {
                    btnForward.animate().alpha(1f).setDuration(300).start()
                    btnRewind.animate().alpha(1f).setDuration(300).start()
                }

                youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                    override fun onCurrentSecond(player: YouTubePlayer, second: Float) {
                        currentSecond = second
                        if (second > 3 && btnForward.alpha == 1f) {
                            hideControls()
                        }
                    }
                })

                youTubePlayerView.setOnClickListener {
                    showControls()
                    btnForward.postDelayed({ hideControls() }, 3000)
                }

                btnRewind.setOnClickListener {
                    val rewindTime = (currentSecond - 5).coerceAtLeast(0f)
                    youTubePlayer.seekTo(rewindTime)
                }

                btnForward.setOnClickListener {
                    youTubePlayer.seekTo(currentSecond + 5)
                }

                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }

    private fun getDataFromFirebase(videoId: String) {
        firestore.collection("materi")
            .whereEqualTo("videoId", videoId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.first()
                    val materi = doc.toObject(MateriModel::class.java)
                    txtPenjelasan.text = materi.penjelasan
                } else {
                    txtPenjelasan.text = "Data materi tidak ditemukan."
                }
            }
            .addOnFailureListener {
                txtPenjelasan.text = "Gagal memuat data dari database."
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun getYoutubeVideoId(url: String): String {
        return when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            else -> ""
        }
    }
}
