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
        val percentage = item.percentage
        val formattedPercentage = if (percentage == 100) {
            String.format("%03d", percentage)
        } else {
            percentage.toString()
        }
        holder.gradeText.text = "$formattedPercentage"
        holder.timestampText.text = formatDate(item.timestamp)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }
}