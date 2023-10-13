package com.ooolrs.taskit

import java.util.*
import java.util.concurrent.TimeUnit
import android.content.Context
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DailyTaskAssignmentWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val databaseReference2: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    val databaseReference3: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser!!.uid).child("badge")
    val completedTasks = ArrayList<String>()

    override fun doWork(): Result {
        Toast.makeText(applicationContext,"Do Work Executed", Toast.LENGTH_SHORT).show()
        try {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tasks")


            if (currentUser != null) {
                val userId = currentUser.uid

                // Retrieve available tasks from Firebase
                getAvailableTasksNotCompleted(databaseReference3)
                getAvailableTasksFromDatabase(databaseReference) { availableTasks ->
                    // Assign tasks to the current user
                    assignTasksToUser(databaseReference2, userId, availableTasks)
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun getAvailableTasksFromDatabase(databaseReference: DatabaseReference, callback: (List<Task>) -> Unit) {
        val availableTasks = mutableListOf<Task>()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (taskSnapshot in dataSnapshot.children) {

                        val taskId = taskSnapshot.key
                        //Toast.makeText(applicationContext, taskId, Toast.LENGTH_SHORT).show()
                       // val id = taskSnapshot.child("badgeId").getValue(String::class.java)
                        val title = taskSnapshot.child("title").getValue(String::class.java)
                        val description = taskSnapshot.child("description").getValue(String::class.java)
                        val points = taskSnapshot.child("points").getValue(Int::class.java)


                        if (taskId != null && title != null && description != null && points != null) {
                            if(taskId in completedTasks){
                            val task = Task(taskId, title, description, points)
                            availableTasks.add(task)
                            }
                        }
                        //Toast.makeText(applicationContext, title.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                callback(availableTasks)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun getAvailableTasksNotCompleted(databaseReference3: DatabaseReference) {

        databaseReference3.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (taskSnapshot in dataSnapshot.children) {
                                // Add the name of the child to the array
                        if(taskSnapshot.value == false) {
                            completedTasks.add(taskSnapshot.key.toString())
                            Toast.makeText(
                                applicationContext,
                                taskSnapshot.key.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun assignTasksToUser(
        databaseReference: DatabaseReference,
        userId: String,
        availableTasks: List<Task>
    ) {
        // Randomly select a specified number of tasks from availableTasks
        // and assign them to the current user in the database

        val tasksToAssign = getRandomTasks(availableTasks, 4)
        val assignedTasksReference = databaseReference.child(userId)

        assignedTasksReference.child("assignedTasks").setValue("1")
        assignedTasksReference.child("assignedTasks").removeValue();


        for (task in tasksToAssign) {
            // Assuming you want to store assigned tasks as child keys with a boolean value (e.g., "task1": true)
            assignedTasksReference.child("assignedTasks").child(task.badgeId).setValue(true)
            //assignedTasksReference.child("badge").child(task.badgeId).setValue(false)

        }// Assign 3 tasks per user

        // Update the "assignedTasks" section in the database for the user
        // Implement the logic to assign tasks to the current user in your Firebase database
    }

    private fun getRandomTasks(availableTasks: List<Task>, count: Int): List<Task> {
        if (count >= availableTasks.size) {
            return availableTasks
        }
        val shuffledTasks = availableTasks.shuffled()
        return shuffledTasks.subList(0, count)
    }
}
