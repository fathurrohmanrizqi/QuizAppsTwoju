package com.fathurrohman.piquizapps

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fathurrohman.piquizapps.databinding.QuizItemRecyclerRowBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class QuizListAdapter(private val quizModelList : List<QuizModel>) :
    RecyclerView.Adapter<QuizListAdapter.MyViewHolder>() {
    class MyViewHolder(private val binding: QuizItemRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(model: QuizModel){
            binding.apply {
                quizTitleText.text = model.title
                quizSubtitleText.text = model.subtitle
                quizTimeText.text = model.time + " min"
                root.setOnClickListener {
                    Log.d("berhasil klik","")
                    getAllQuestions(model.id) { questions ->
                        QuizMainAct.questionModelList = questions
                        QuizMainAct.time = model.time
                        val intent = Intent(root.context, QuizMainAct::class.java)
                        intent.putExtra("QUIZ_TITLE", model.title)
                        root.context.startActivity(intent)
                    }
                }
            }
        }
        fun getAllQuestions(quizDocumentId: String, callback: (List<QuestionModel>) -> Unit) {
            val db = Firebase.firestore
            val listQuestions = mutableListOf<QuestionModel>()

            db.collection("quizzes").document(quizDocumentId).collection("questions")
                .get()
                .addOnSuccessListener { result ->
                    Log.d("berhasil ambil data","")
                    for (document in result) {
                        Log.d(document.id,"pertanyaan")
                        val question = document.toObject(QuestionModel::class.java)
                        listQuestions.add(question)
                    }
                    listQuestions.shuffle()
                    callback(listQuestions) // Panggil callback saat data selesai dimuat
                }
                .addOnFailureListener { exception ->
                    Log.d("Gagal boss", "Error getting documents: ", exception)
                    callback(emptyList()) // Kembalikan list kosong jika gagal
                }
        }


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QuizItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizModelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //sesuai dengan index jika index 1, position 0
        holder.bind(quizModelList[position])
    }
}