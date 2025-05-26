package com.fathurrohman.piquizapps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        val txtName = findViewById<TextView>(R.id.txtName)
        val txtClass = findViewById<TextView>(R.id.txtClass)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val kelas = document.getString("class")

                        txtName.text = name
                        txtClass.text = kelas
                    } else {
                        txtName.text = "Nama tidak ditemukan"
                        txtClass.text = "Kelas tidak ditemukan"
                    }
                }
                .addOnFailureListener {
                    txtName.text = "Gagal mengambil nama"
                    txtClass.text = "Gagal mengambil kelas"
                }
        }

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
