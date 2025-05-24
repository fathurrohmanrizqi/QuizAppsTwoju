package com.fathurrohman.piquizapps

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryQuizMainAct : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var btnOptions: List<Button>
    private lateinit var nextButton: Button
    private lateinit var indicatorTextView: TextView

    private var currentIndex = 0
    private val quizHistory = mutableListOf<QuizAnswer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_quiz_main)

        questionTextView = findViewById(R.id.question_textview)
        indicatorTextView = findViewById(R.id.question_indicator_textview)
        btnOptions = listOf(
            findViewById(R.id.btn0),
            findViewById(R.id.btn1),
            findViewById(R.id.btn2),
            findViewById(R.id.btn3)
        )
        nextButton = findViewById(R.id.next_btn)

        nextButton.setOnClickListener {
            if (currentIndex < quizHistory.size - 1) {
                currentIndex++
                showQuestion()
            } else {
                Toast.makeText(this, "Selesai!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        val documentId = intent.getStringExtra("documentId") ?: return
        loadQuizResultByDocumentId(documentId)
    }

    private fun loadQuizResultByDocumentId(documentId: String) {
        FirebaseFirestore.getInstance()
            .collection("quiz_results")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val score = document.getLong("score")?.toInt() ?: 0
                    val timestamp = document.getLong("timestamp") ?: 0L
                    val answers = document["answers"] as? List<Map<String, Any>> ?: emptyList()

                    Log.d("Firestore", "Score: $score, Timestamp: $timestamp, Answers: $answers")

                    // Misal kamu ingin tampilkan semua jawaban ke quizHistory:
                    quizHistory.clear()
                    for (answer in answers) {
                        val question = answer["question"] as? String ?: continue
                        val correctAnswer = answer["correctAnswer"] as? String ?: ""
                        val selectedAnswer = answer["selectedAnswer"] as? String ?: ""
                        val isCorrect = answer["isCorrect"] as? Boolean ?: false
                        val options = (answer["options"] as? List<*>)?.map { it.toString() } ?: emptyList()

                        quizHistory.add(
                            QuizAnswer(
                                question = question,
                                correctAnswer = correctAnswer,
                                selectedAnswer = selectedAnswer,
                                isCorrect = isCorrect,
                                options = options
                            )
                        )
                    }

                    if (quizHistory.isNotEmpty()) {
                        currentIndex = 0
                        showQuestion()
                    } else {
                        Toast.makeText(this, "Tidak ada data jawaban", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Dokumen tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal mengambil dokumen", e)
            }
    }


    private fun showQuestion() {
        val currentQuiz = quizHistory[currentIndex]
        questionTextView.text = currentQuiz.question
        indicatorTextView.text = "Question ${currentIndex + 1}/${quizHistory.size}"

        for (i in btnOptions.indices) {
            val btn = btnOptions[i]
            val optionText = currentQuiz.options.getOrNull(i) ?: "-"
            btn.text = optionText

            // Reset to default
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.cream))
            btn.setTextColor(Color.BLACK)

            when {
                optionText == currentQuiz.correctAnswer && optionText == currentQuiz.selectedAnswer -> {
                    btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                    btn.setTextColor(Color.WHITE)
                }
                optionText == currentQuiz.correctAnswer -> {
                    btn.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
                    btn.setTextColor(Color.WHITE)
                }
                optionText == currentQuiz.selectedAnswer -> {
                    btn.setBackgroundColor(Color.RED)
                    btn.setTextColor(Color.WHITE)
                }
            }
        }
    }

    data class QuizAnswer(
        val correctAnswer: String,
        val isCorrect: Boolean,
        val options: List<String>,
        val question: String,
        val selectedAnswer: String,
    )
}
