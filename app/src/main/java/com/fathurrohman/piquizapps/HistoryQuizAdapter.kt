package com.fathurrohman.piquizapps

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fathurrohman.piquizapps.R
import com.fathurrohman.piquizapps.QuizResult
import java.text.SimpleDateFormat
import java.util.*

class HistoryQuizAdapter(
    private val list: List<QuizResult>,
    private val onItemClick: (QuizResult) -> Unit
) : RecyclerView.Adapter<HistoryQuizAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.materi_title_text)
        val gradeText: TextView = view.findViewById(R.id.grades)
        val timestampText: TextView = view.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_quiz_recycler_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.titleText.text = item.title
        holder.gradeText.text = item.score.toString()
        holder.timestampText.text = formatDate(item.timestamp)

        // Tambahkan event klik
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}