package com.ooolrs.taskit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class TaskAssignReciver : BroadcastReceiver() {
    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val databaseReference2: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tasks")
    val databaseReference3: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users").child(currentUser!!.uid).child("badge")
    val userId = currentUser!!.uid

    override fun onReceive(context: Context?, intent: Intent?) {
        // Execute your function here
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotification(context, notificationManager)

        Toast.makeText(context, "Do Work Executed", Toast.LENGTH_SHORT).show()

        if (currentUser != null) {
            // Retrieve available tasks from Firebase
            getAvailableTasksNotCompleted(databaseReference3, context)
            /**/
        }
    }

    private fun getAvailableTasksFromDatabase(
        databaseReference: DatabaseReference,
        context: Context?,
        completedTasks: ArrayList<String>,
        callback: (List<Task>) -> Unit,
    ) {
        val availableTasks = mutableListOf<Task>()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (taskSnapshot in dataSnapshot.children) {

                        val taskId = taskSnapshot.key
                        //Toast.makeText(context, taskId, Toast.LENGTH_SHORT).show()
                        // val id = taskSnapshot.child("badgeId").getValue(String::class.java)
                        val title = taskSnapshot.child("title").getValue(String::class.java)
                        val description =
                            taskSnapshot.child("description").getValue(String::class.java)
                        val points = taskSnapshot.child("points").getValue(Int::class.java)


                        if (taskId != null && title != null && description != null && points != null) {
                            //Toast.makeText(context, taskId.toString()+"---"+completedTasks, Toast.LENGTH_SHORT).show()
                            if (taskId in completedTasks) {
                                val task = Task(taskId, title, description, points)
                                availableTasks.add(task)

                            }
                        }

                    }
                }
                callback(availableTasks)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun getAvailableTasksNotCompleted(
        databaseReference3: DatabaseReference,
        context: Context?
    ) {
        val completedTasks2 = ArrayList<String>()
        databaseReference3.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (taskSnapshot in dataSnapshot.children) {
                        // Add the name of the child to the array
                        if (taskSnapshot.value == false) {
                            completedTasks2.add(taskSnapshot.key.toString())
                        }
                    }
                }

                getAvailableTasksFromDatabase(
                    databaseReference,
                    context,
                    completedTasks2
                ) { availableTasks ->
                    // Assign tasks to the current user
                    assignTasksToUser(databaseReference2, userId, availableTasks, context)
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
        availableTasks: List<Task>, context: Context?
    ) {
        // Randomly select  specified number of tasks from availableTasks
        // and assign them to the current user in the database

        val tasksToAssign = getRandomTasks(availableTasks, 4, context)
        val assignedTasksReference = databaseReference.child(userId)

        assignedTasksReference.child("assignedTasks").setValue("1")
        assignedTasksReference.child("assignedTasks").removeValue();
        /*
                Toast.makeText(
                    context,
                    tasksToAssign.toString(),
                    Toast.LENGTH_SHORT
                ).show()*/

        for (task in tasksToAssign) {
            // Assuming you want to store assigned tasks as child keys with a boolean value (e.g., "task1": true)
            assignedTasksReference.child("assignedTasks").child(task.badgeId).setValue(false)
            //assignedTasksReference.child("badge").child(task.badgeId).setValue(false)

        }// Assign 3 tasks per user

        // Update the "assignedTasks" section in the database for the user
        // Implement the logic to assign tasks to the current user in your Firebase database
    }

    private fun getRandomTasks(
        availableTasks: List<Task>,
        count: Int,
        context: Context?
    ): List<Task> {
        if (count >= availableTasks.size) {
            return availableTasks
        }/*
        Toast.makeText(
            context,
            availableTasks.toString(),
            Toast.LENGTH_SHORT
        ).show()*/
        val shuffledTasks = availableTasks.shuffled()
        return shuffledTasks.subList(0, count)
    }

    private fun createNotification(context: Context, notificationManager: NotificationManager) {
        // Define notification content
        val channelId = "Task Reset !"
        val notificationTitle = "! New task Assigned !"
        val notificationText = "Click now to check out your new tasks."

        // Create a notification channel (for Android 8.0 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setAutoCancel(true) // Close the notification when tapped

        // Define the intent to open an activity when the notification is tapped
        val resultIntent = Intent(context, SplashScreen::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            resultIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.setContentIntent(pendingIntent)

        // Notify the notification
        val notificationId = 1 // You can use a unique ID for each notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}