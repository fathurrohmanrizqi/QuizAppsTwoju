package com.fathurrohman.piquizapps

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fathurrohman.piquizapps.databinding.ActivityQuizBinding
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore

class QuizActivity : AppCompatActivity() {
    lateinit var binding : ActivityQuizBinding
    lateinit var quizModelList : MutableList<QuizModel>
    lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        quizModelList = mutableListOf()
        getDataFromFirebase()

        btnBack.setOnClickListener{
            finish()
        }
    }

    private fun setupRecyclerView(){
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase(){
        val db = Firebase.firestore
        binding.progressBar.visibility = View.VISIBLE

        db.collection("quizzes")

            .get()
            .addOnSuccessListener { result ->
                quizModelList.clear()
                for (document in result) {
                    val quiz = document.toObject(QuizModel::class.java)
                    quiz.id = document.id
                    Log.d("Daftar Quiz", "${quiz.title} => ${quiz.id}")
                    quizModelList.add(quiz)
                    binding.progressBar.visibility = View.GONE
                }
                setupRecyclerView()
            }
            .addOnFailureListener { exception ->
                Log.d("Gagal boss", "Error getting documents: ", exception)
            }

    }
}