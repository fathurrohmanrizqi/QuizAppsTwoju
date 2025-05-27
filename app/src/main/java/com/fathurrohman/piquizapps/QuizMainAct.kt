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

    private var quizTitle: String = ""

    lateinit var binding:ActivityQuizMainBinding

    var currentQuestionIndex = 0;
    var selectedAnswer = "";
    var score = 0;

    val userAnswers = mutableListOf<Map<String, Any>>()
    val userSelectedAnswers = mutableListOf<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        quizTitle = intent.getStringExtra("QUIZ_TITLE") ?: "Untitled Quiz"
        binding.apply {
            btn0.setOnClickListener(this@QuizMainAct)
            btn1.setOnClickListener(this@QuizMainAct)
            btn2.setOnClickListener(this@QuizMainAct)
            btn3.setOnClickListener(this@QuizMainAct)
            nextBtn.setOnClickListener(this@QuizMainAct)
            prevBtn.setOnClickListener(this@QuizMainAct)
        }
        userSelectedAnswers.clear()
        repeat(questionModelList.size) {
            userSelectedAnswers.add(null)
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
        val prevAnswer = userSelectedAnswers[currentQuestionIndex]
        selectedAnswer = prevAnswer ?: ""
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
        val optionButtons = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)
        optionButtons.forEach { btn ->
            if (btn.text.toString() == selectedAnswer) {
                btn.setBackgroundColor(getColor(R.color.blue)) // warna terpilih
            } else {
                btn.setBackgroundColor(getColor(R.color.cream)) // default
            }
        }
    }

    override fun onClick(view: View?) {
        val clickedBtn = view as Button

        when (clickedBtn.id) {
            R.id.next_btn -> {
                val currentQuestion = questionModelList[currentQuestionIndex]
                userSelectedAnswers[currentQuestionIndex] = selectedAnswer
                val isCorrect = selectedAnswer == currentQuestion.correct
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

                selectedAnswer = ""
                currentQuestionIndex++

                if (currentQuestionIndex == questionModelList.size) {
                    finishQuiz()
                } else {
                    loadQuestions()
                }
            }

            R.id.prev_btn -> {
                if (currentQuestionIndex > 0) {
                    userSelectedAnswers[currentQuestionIndex] = selectedAnswer
                    currentQuestionIndex--
                    selectedAnswer = userSelectedAnswers[currentQuestionIndex] ?: ""
                    loadQuestions()
                }
            }

            else -> {
                selectedAnswer = clickedBtn.text.toString()
                highlightSelectedButton(clickedBtn)
            }
        }
    }

    private fun highlightSelectedButton(selectedButton: Button) {
        val buttons = listOf(binding.btn0, binding.btn1, binding.btn2, binding.btn3)
        for (btn in buttons) {
            btn.setBackgroundColor(getColor(R.color.cream)) // Reset semua
        }
        selectedButton.setBackgroundColor(getColor(R.color.blue)) // Tandai yang dipilih
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
                "title" to quizTitle,
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