package com.ooolrs.taskit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CustomAdapter(private val dataset: List<Task>, private val onItemClicked: buttonOnCard)  : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTask: TextView = view.findViewById(R.id.titleTask)
        val desTask: TextView = view.findViewById(R.id.desTask)
        val pointTask: TextView = view.findViewById(R.id.pointTask)
        val taskComBtn: AppCompatButton = view.findViewById(R.id.taskcompBtn)
        val taskComanim: LottieAnimationView = view.findViewById(R.id.taskcompletedanim)
        val coinlayout: LinearLayout = view.findViewById(R.id.taskpointll)
        val view: View = view
        val context = view.context

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


        auth = FirebaseAuth.getInstance()


        val userid = auth.currentUser?.uid.toString()
        val database = FirebaseDatabase.getInstance()
        dbref = database.getReference("users").child(userid).child("assignedTasks")

        dbref.get().addOnSuccessListener {
            if(it.exists()) {
                val isDone = it.child(item.badgeId).value
                if(isDone==true){
                    holder.taskComanim.visibility = View.VISIBLE
                    holder.taskComBtn.visibility = View.GONE
                    holder.coinlayout.visibility = View.GONE
                }
            }
        }


        holder.taskComBtn.setOnClickListener { onItemClicked.onItemClicked(item.badgeId,item.points,holder.view) }


    }

    override fun getItemCount() = dataset.size
}
