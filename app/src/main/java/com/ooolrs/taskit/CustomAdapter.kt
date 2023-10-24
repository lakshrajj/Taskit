package com.ooolrs.taskit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val dataset: List<Task>, private val onItemClicked: buttonOnCard)  : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTask: TextView = view.findViewById(R.id.titleTask)
        val desTask: TextView = view.findViewById(R.id.desTask)
        val pointTask: TextView = view.findViewById(R.id.pointTask)
        val taskComBtn: AppCompatButton = view.findViewById(R.id.taskcompBtn)
        val view: View = view


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taskitem_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        holder.titleTask.text = item.title
        holder.desTask.text = "Task : "+item.description
        holder.pointTask.text = " : "+item.points.toString()
        holder.taskComBtn.setOnClickListener{onItemClicked.onItemClicked(item.badgeId,item.points,holder.view)}
    }

    override fun getItemCount() = dataset.size
}
