package com.ooolrs.taskit

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.ooolrs.taskit.databinding.ActivityHomeBinding
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList
import java.util.Calendar
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ProgressDialog: Dialog
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    var assignedTasksAry = ArrayList<String>()
    val a = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("users").child(auth.currentUser!!.uid).child("assignedTasks")
        databaseReference2 = database.getReference("tasks")
        assignedTasksAry.add("Task")


        if (!isTaskScheduled(auth.currentUser!!.uid)) {
            // Schedule a new periodic task for this user
            schedulePeriodicTaskForUser(auth.currentUser!!.uid)
            setTaskScheduled(auth.currentUser!!.uid, true)
        }

         // Replace with your specific data path

        ProgressDialog = Dialog(this)
        ProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        ProgressDialog.setContentView(R.layout.loading_dialog)
        ProgressDialog.setCancelable(false)


        lateinit var toggle: ActionBarDrawerToggle

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("task_schedule_prefs", Context.MODE_PRIVATE)


        ProgressDialog.show()
        getAssignedTaskAry()


        // Schedule the work

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle("")


        toggle = ActionBarDrawerToggle(this,binding.drawer,binding.toolbar,R.string.open,R.string.close)
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()



        binding.nav.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item clicks here
            when (menuItem.itemId) {
                R.id.logoutMenu -> {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)

                }
                R.id.nav_Settings -> {
                    // Handle navigation to Item 2
                }
            }
            // Close the drawer
            binding.drawer.closeDrawers()
            true
        }
        /*
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

*/
    }

    private fun schedulePeriodicTaskForUser(uid: String) {


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(DailyTaskAssignmentWorker::class.java, 24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(uid, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest)


    }

    private fun isTaskScheduled(uid: String): Boolean {
        return sharedPreferences.getBoolean(uid, false)

    }

    private fun setTaskScheduled(uid: String, scheduled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(uid, scheduled)
        editor.apply()
    }


    private fun loadRecycelView() {
       binding.recyclerViewAssignedTasks.adapter = CustomAdapter(a)
        ProgressDialog.dismiss()

    }

    private fun getAssignedTaskAry() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    assignedTasksAry.add(childSnapshot.key.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
        getAssignedTask()
    }
    private fun getAssignedTask() {


        databaseReference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {/*
                   if(assignedTasksAry.contains(childSnapshot.key.toString())){
                       Toast.makeText(this@HomeActivity, childSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show()
                       //a.add(childSnapshot.getValue(Task::class.java)!!)
                   }*/

                    val taskId = childSnapshot.key
                    //Toast.makeText(applicationContext, taskId, Toast.LENGTH_SHORT).show()
                    // val id = taskSnapshot.child("badgeId").getValue(String::class.java)
                    val title = childSnapshot.child("title").getValue(String::class.java)
                    val description = childSnapshot.child("description").getValue(String::class.java)
                    val points = childSnapshot.child("points").getValue(Int::class.java)


                    if(assignedTasksAry.contains(taskId)){
                        if (taskId != null && title != null && description != null && points != null) {
                            val task = Task(taskId, title, description, points)
                            a.add(task)
                            Toast.makeText(this@HomeActivity,a.toString()+"loading recycler", Toast.LENGTH_SHORT).show()

                        }
                    }

                }

                loadRecycelView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })

    }


}