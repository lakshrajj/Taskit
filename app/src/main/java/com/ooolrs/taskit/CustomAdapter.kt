package com.ooolrs.taskit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ooolrs.taskit.databinding.ActivityHomeBinding

class CustomAdapter(private val dataset: List<Task>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTask: TextView = view.findViewById(R.id.titleTask)
        val desTask: TextView = view.findViewById(R.id.desTask)
        val pointTask: TextView = view.findViewById(R.id.pointTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taskitem_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        holder.titleTask.text = item.title
        holder.desTask.text = "Task : "+item.description
        holder.pointTask.text = "Point : "+item.points.toString()
    }

    override fun getItemCount() = dataset.size
}
