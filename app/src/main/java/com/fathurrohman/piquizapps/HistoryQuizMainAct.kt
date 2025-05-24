package com.fathurrohman.piquizapps

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryQuizMainAct : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var btnOptions: List<Button>
    private lateinit var nextButton: Button
    private lateinit var indicatorTextView: TextView

    private var currentIndex = 0
    private val quizHistory = mutableListOf<QuizHistory>()

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

         loadUserAnswersFromFirestore()
        fetchAllQuizzesAndDisplay()
    }

    private fun fetchAllQuizzesAndDisplay() {
        val db = FirebaseFirestore.getInstance()
        db.collection("quizzes")
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val questionsData = document["questions"]
                    Log.d("Firestore", "Isi questions: $questionsData")

                    val questions = questionsData as? List<Map<String, Any>> ?: continue
                    quizHistory.clear()

                    for (q in questions) {
                        val questionText = q["question"] as? String ?: ""
                        val correct = q["correct"] as? String ?: ""
                        val options = q["options"] as? List<String> ?: listOf()

                        Log.d("QuizDebug", "Q: $questionText | Opt: $options | Ans: $correct")

                        quizHistory.add(
                            QuizHistory(
                                question = questionText,
                                options = options,
                                correctAnswer = correct,
                                selectedAnswer = ""
                            )
                        )
                    }

                    if (quizHistory.isNotEmpty()) {
                        currentIndex = 0
                        showQuestion()
                    } else {
                        Toast.makeText(this, "Quiz kosong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Gagal ambil quiz", e)
                Toast.makeText(this, "Gagal ambil data quiz", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadUserAnswersFromFirestore() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance()
                .collection("user_answers")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val question = doc.getString("question") ?: continue
                        val correctAnswer = doc.get("correctAnswer")
                        val selectedAnswer = doc.get("selectedAnswer")
                        val options = (doc["options"] as? List<*>)?.map { it.toString() } ?: emptyList()

                        quizHistory.add(
                            QuizHistory(
                                question = question,
                                options = options,
                                correctAnswer = correctAnswer.toString(),
                                selectedAnswer = selectedAnswer.toString()
                            )
                        )
                    }

                    if (quizHistory.isNotEmpty()) {
                        showQuestion()
                    } else {
                        Toast.makeText(this, "Belum ada data jawaban!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HistoryQuiz", "Gagal load jawaban", e)
                    Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
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

            // Reset warna default
            btn.setBackgroundColor(getColor(R.color.cream))
            btn.setTextColor(Color.BLACK)

            // Highlight berdasarkan jawaban
            if (optionText == currentQuiz.selectedAnswer && optionText != currentQuiz.correctAnswer) {
                btn.setBackgroundColor(Color.RED) // Salah dipilih user
                btn.setTextColor(Color.WHITE)
                Log.d("lah ga kebaca","")
            } else if (optionText == currentQuiz.correctAnswer) {
                btn.setBackgroundColor(getColor(R.color.blue)) // Jawaban benar
                btn.setTextColor(Color.WHITE)
                Log.d("lah ga ini juga ga kebaca","")
            }
        }
    }

    data class QuizHistory(
        val question: String,
        val options: List<String>,
        val correctAnswer: String,
        val selectedAnswer: String
    )
}
