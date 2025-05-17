package com.fathurrohman.piquizapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fathurrohman.piquizapps.R

class MateriAdapter(
    private val materiList: List<MateriModel>,
    private val onItemClick: (MateriModel) -> Unit
) : RecyclerView.Adapter<MateriAdapter.MateriViewHolder>() {

    inner class MateriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val judulText: TextView = itemView.findViewById(R.id.materi_title_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.materi_item_recycler_row, parent, false)
        return MateriViewHolder(view)
    }

    override fun onBindViewHolder(holder: MateriViewHolder, position: Int) {
        val materi = materiList[position]
        holder.judulText.text = materi.judul
        holder.itemView.setOnClickListener {
            onItemClick(materi)
        }
    }

    override fun getItemCount(): Int = materiList.size
}