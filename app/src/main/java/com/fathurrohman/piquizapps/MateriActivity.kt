package com.fathurrohman.piquizapps

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MateriActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var materiList: ArrayList<MateriModel>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materi)

        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        materiList = arrayListOf()

        databaseRef = FirebaseDatabase.getInstance().getReference("materi")

        progressBar.visibility = View.VISIBLE
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                materiList.clear()
                for (data in snapshot.children) {
                    val materi = data.getValue(MateriModel::class.java)
                    materi?.let { materiList.add(it) }
                }
                recyclerView.adapter = MateriAdapter(materiList) { materi ->
                    val intent = Intent(this@MateriActivity, MateriMainActivity::class.java)
                    intent.putExtra("judul", materi.judul)
                    intent.putExtra("videoUrl", materi.videoUrl)
                    intent.putExtra("penjelasan", materi.penjelasan)
                    startActivity(intent)
                }
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }
}