package com.fathurrohman.piquizapps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fathurrohman.piquizapps.HistoryQuizAdapter
import com.fathurrohman.piquizapps.QuizResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryQuizActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: HistoryQuizAdapter
    private val quizResultList = mutableListOf<QuizResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_quiz)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)

        adapter = HistoryQuizAdapter(quizResultList) { selectedQuiz ->
            val intent = Intent(this, HistoryQuizMainAct::class.java)
            intent.putExtra("documentId", selectedQuiz.documentId)
            intent.putExtra("title", selectedQuiz.title)
            intent.putExtra("score", selectedQuiz.percentage)
            intent.putExtra("timestamp", selectedQuiz.timestamp)
            startActivity(intent)
        }

        btnBack.setOnClickListener{
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchQuizHistory()
    }

    private fun fetchQuizHistory() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            progressBar.visibility = View.VISIBLE
            val db = FirebaseFirestore.getInstance()
            db.collection("quiz_results")
                .whereEqualTo("userId", user.uid)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    quizResultList.clear()
                    for (document in result) {
                        val documentId = document.id // Inilah cara yang benar untuk mengambil ID dari setiap dokumen
                        val title = document.getString("title") ?: "Untitled Quiz"
                        val percentage = (document["percentage"] as? Long)?.toInt() ?: 0
                        val timestamp = document["timestamp"] as? Long ?: 0L
                        quizResultList.add(QuizResult(documentId, title, percentage, timestamp))
                    }

                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Log.e("HistoryQuizActivity", "Failed to fetch quiz results", e)
                    progressBar.visibility = View.GONE
                }
        }
    }
}