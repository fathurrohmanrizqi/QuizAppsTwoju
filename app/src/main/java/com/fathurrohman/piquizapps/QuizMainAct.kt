package com.fathurrohman.piquizapps

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fathurrohman.piquizapps.databinding.ActivityQuizBinding
import com.fathurrohman.piquizapps.databinding.ActivityQuizMainBinding
import com.fathurrohman.piquizapps.databinding.ScoreDialogBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class QuizMainAct : AppCompatActivity(),View.OnClickListener{

    companion object{
        var questionModelList : List<QuestionModel> = listOf()
        var time : String = ""
    }

    lateinit var binding:ActivityQuizMainBinding

    var currentQuestionIndex = 0;
    var selectedAnswer = "";
    var score = 0;

    val userAnswers = mutableListOf<Map<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btn0.setOnClickListener(this@QuizMainAct)
            btn1.setOnClickListener(this@QuizMainAct)
            btn2.setOnClickListener(this@QuizMainAct)
            btn3.setOnClickListener(this@QuizMainAct)
            nextBtn.setOnClickListener(this@QuizMainAct)
        }
        loadQuestions()
        startTimer()
    }

    private fun startTimer(){
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished /1000
                val minutes = seconds/60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes,remainingSeconds)
            }

            override fun onFinish() {
//                Finish
            }

        }.start()
    }

    private fun loadQuestions(){
        selectedAnswer = ""
        if(currentQuestionIndex == questionModelList.size){
            finishQuiz()
            return
        }
        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex+1}/ ${questionModelList.size} "
            questionProgressIndicator.progress =
                ( currentQuestionIndex.toFloat() / questionModelList.size.toFloat() * 100 ).toInt()
            questionTextview.text = questionModelList[currentQuestionIndex].question
            btn0.text = questionModelList[currentQuestionIndex].options[0]
            btn1.text = questionModelList[currentQuestionIndex].options[1]
            btn2.text = questionModelList[currentQuestionIndex].options[2]
            btn3.text = questionModelList[currentQuestionIndex].options[3]
        }
    }

    override fun onClick(view: View?) {
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.cream))
            btn1.setBackgroundColor(getColor(R.color.cream))
            btn2.setBackgroundColor(getColor(R.color.cream))
            btn3.setBackgroundColor(getColor(R.color.cream))
        }
        val clickedBtn = view as Button

        // Jika tombol NEXT ditekan
        if (clickedBtn.id == R.id.next_btn) {
            val currentQuestion = questionModelList[currentQuestionIndex]
            val isCorrect = selectedAnswer == currentQuestion.correct
//            val isOption =
            if (isCorrect) {
                score++
            }

            val answerRecord = mapOf(
                "question" to currentQuestion.question,
                "selectedAnswer" to selectedAnswer,
                "correctAnswer" to currentQuestion.correct,
                "isCorrect" to isCorrect,
                "options" to currentQuestion.options
            )
            userAnswers.add(answerRecord)

            // Reset selectedAnswer untuk soal berikutnya
            selectedAnswer = ""
            currentQuestionIndex++
            loadQuestions()
        } else {
            // Set jawaban yang dipilih dan warnai tombolnya
            selectedAnswer = clickedBtn.text.toString()
            clickedBtn.setBackgroundColor(getColor(R.color.blue)) // atau warna yang kamu mau
        }
    }

    private fun finishQuiz(){
        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"
            if (percentage > 60) {
                scoreTitle.text = "Congrats! You have passed"
                scoreTitle.setTextColor(ContextCompat.getColor(this@QuizMainAct, R.color.blue))
            } else {
                scoreTitle.text = "Oh Noo! You have failed"
                scoreTitle.setTextColor(ContextCompat.getColor(this@QuizMainAct, R.color.red))
            }
            scoreSubtitle.text = "$score out of $totalQuestions are correct"
            finishBtn.setOnClickListener {
                saveResultToFirestore(score, totalQuestions, percentage)
                saveAnswersToFirestore()
                finish()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()

    }

    private fun saveResultToFirestore(score: Int, totalQuestions: Int, percentage: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val quizResult = hashMapOf(
                "userId" to user.uid,
                "score" to score,
                "totalQuestions" to totalQuestions,
                "percentage" to percentage,
                "timestamp" to System.currentTimeMillis(),
                "answers" to userAnswers
            )
            db.collection("quiz_results")
                .add(quizResult)
                .addOnSuccessListener {
                    Log.d("QuizMainAct", "Quiz result saved for user: ${user.uid}")
                }
                .addOnFailureListener { e ->
                    Log.e("QuizMainAct", "Error saving quiz result", e)
                }
        } else {
            Log.e("QuizMainAct", "User not logged in")
        }
    }

    private fun saveAnswersToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val userId = user.uid
            for (answer in userAnswers) {
                val answerWithUser = HashMap(answer)
                answerWithUser["userId"] = userId
                db.collection("user_answers")
                    .add(answerWithUser)
                    .addOnSuccessListener {
                        Log.d("QuizMainAct", "Saved answer to Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e("QuizMainAct", "Failed to save answer", e)
                    }
            }
        }
    }
}