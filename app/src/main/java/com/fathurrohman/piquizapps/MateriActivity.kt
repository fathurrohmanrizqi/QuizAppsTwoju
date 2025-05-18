package com.fathurrohman.piquizapps

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MateriActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var materiAdapter: MateriAdapter
    private val materiList = mutableListOf<MateriModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materi)

        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        materiAdapter = MateriAdapter(materiList) { materi ->
            val intent = Intent(this@MateriActivity, MateriMainActivity::class.java).apply {
                putExtra("judul", materi.judul)
                putExtra("videoUrl", materi.videoUrl)
                putExtra("penjelasan", materi.penjelasan)
            }
            startActivity(intent)
        }

        recyclerView.adapter = materiAdapter

        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        progressBar.visibility = View.VISIBLE

        FirebaseFirestore.getInstance().collection("materi")
            .get()
            .addOnSuccessListener { result ->
                materiList.clear()
                for (document in result) {
                    val materi = document.toObject(MateriModel::class.java)
                    materiList.add(materi)
                }
                materiAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
            }
    }
}
