package com.example.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Filter
import com.example.photoeditor.R

class FilterAdapter(
    private val filters: List<Filter>,
    private val listener: OnFilterClickListener
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    interface OnFilterClickListener {
        fun onFilterClick(filter: Filter)
    }

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.rotate_text)
        val button: ImageButton = itemView.findViewById(R.id.rotate_button)

        init {
            button.setOnClickListener {
                listener.onFilterClick(filters[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.button.setImageResource(filters[position].icon_source)
        holder.text.setText(filters[position].string_source)
    }

    override fun getItemCount(): Int {
        return filters.size
    }
}
