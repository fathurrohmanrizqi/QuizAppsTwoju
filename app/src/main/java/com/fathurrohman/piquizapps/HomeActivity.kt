package com.fathurrohman.piquizapps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Button binding
        val btnBelajar = findViewById<Button>(R.id.btnBelajar)
        val btnQuiz = findViewById<Button>(R.id.btnQuiz)
        val btnHistoryQuiz = findViewById<Button>(R.id.btnHistoryQuiz)

        // Aksi klik tombol Materi Pembelajaran
        btnBelajar.setOnClickListener {
            val intent = Intent(this, MateriActivity::class.java)
            startActivity(intent)
        }

        // Aksi klik tombol Mulai Kuis
        btnQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        // Aksi klik tombol Histori Kuis
        btnHistoryQuiz.setOnClickListener {
            val intent = Intent(this, HistoryQuizActivity::class.java)
            startActivity(intent)
        }
    }
}
