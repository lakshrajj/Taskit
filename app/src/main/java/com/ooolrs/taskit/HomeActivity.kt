package com.ooolrs.taskit

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.ooolrs.taskit.databinding.ActivityHomeBinding
import java.util.Calendar


class HomeActivity : AppCompatActivity(), buttonOnCard {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ProgressDialog: Dialog
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dbref: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    var assignedTasksAry = ArrayList<String>()
    val a = mutableListOf<Task>()
    var currentPoints = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isFirstLogin = prefs.getBoolean("firstLogin", true)


        val userid = auth.currentUser?.uid.toString()
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("users").child(userid)
        dbref = database.getReference("users").child(userid).child("assignedTasks")
        databaseReference2 = database.getReference("tasks")

        assignedTasksAry.add("Task")


        // Replace with your specific data path

        ProgressDialog = Dialog(this)
        ProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        ProgressDialog.setContentView(R.layout.loading_dialog)
        ProgressDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        ProgressDialog.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        ProgressDialog.setCancelable(false)


        lateinit var toggle: ActionBarDrawerToggle


        val alarmManager2 = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, TaskAssignReciver::class.java) // Replace with your alarm action
        val pendingIntent2 = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

// Calculate the time for midnight
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 0) // Set the hour to midnight (0)
        calendar.set(Calendar.MINUTE, 0) // Set the minute to 0
        calendar.set(Calendar.SECOND, 0) // Set the second to 0

// Check if the midnight time is already past today, and if so, schedule it for the next day
        if (System.currentTimeMillis() > calendar.timeInMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to the next day
        }

        val triggerTime2 = calendar.timeInMillis
        Toast.makeText(this, triggerTime2.toString(), Toast.LENGTH_SHORT).show()

        alarmManager2.set(AlarmManager.RTC, 1000*60, pendingIntent2)


// Calculate the time remaining until midnight
        val currentTime = System.currentTimeMillis()
        val midnightTime = triggerTime2 // The triggerTime from the previous code

        val timeRemaining = midnightTime - currentTime

        val countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Calculate hours, minutes, and seconds remaining
                val seconds = (millisUntilFinished / 1000 % 60).toInt()
                val minutes = (millisUntilFinished / (1000 * 60) % 60).toInt()
                val hours = (millisUntilFinished / (1000 * 60 * 60)).toInt()

                // Update the TextView with the countdown
                binding.countdown.text =
                    "(" + String.format("%02d:%02d:%02d", hours, minutes, seconds) + ")"
            }

            override fun onFinish() {
                // Handle when the countdown is finished (midnight has passed)
                binding.countdown.text = "00:00:00"
            }
        }


// Start the countdown
        countDownTimer.start()


        if (isFirstLogin) {
            alarmManager2.set(AlarmManager.RTC, 1000, pendingIntent2)
            // Update the flag to indicate that the user has logged in
            prefs.edit().putBoolean("firstLogin", false).apply()
            Toast.makeText(this, "New User Detected, Restart App to Load Tasks", Toast.LENGTH_SHORT)
                .show()
        }


        binding.coinloadprogressbar.visibility = View.VISIBLE

        sharedPreferences = getSharedPreferences("task_schedule_prefs", Context.MODE_PRIVATE)
        /*
                val alarmIntent =  Intent(this, TaskAssignReciver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    alarmIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                // Schedule the alarm to trigger every 24 hours
                 // 24 hours in milliseconds
                val triggerTime = SystemClock.elapsedRealtime() + interval

                alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    interval.toLong(),
                    pendingIntent
                )

        */
        ProgressDialog.show()
        getAssignedTaskAry()
        loadpoints()


        // Schedule the work

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle("")


        toggle = ActionBarDrawerToggle(
            this,
            binding.drawer,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigationDrawer()


        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.availpointslayout.setOnClickListener {
            val intent = Intent(this, RedeemPoints::class.java)
            startActivity(intent)
        }

    }

    private fun loadpoints() {

        databaseReference.get().addOnSuccessListener {
            if (it.exists()) {
                val currentpoints = it.child("totalPoints").value
                currentPoints = currentpoints.toString().toInt()
                binding.coinloadprogressbar.visibility = View.GONE
                binding.availPoints.text = " " + currentpoints.toString()
            }
        }
    }

    private fun setupNavigationDrawer() {
        binding.nav.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item clicks here
            when (menuItem.itemId) {
                R.id.logoutMenu -> {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_Settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_redeem -> {
                    val intent = Intent(this, RedeemPoints::class.java)
                    startActivity(intent)
                }

                R.id.nav_rewardhistory -> {
                    val intent = Intent(this, RewardHistory::class.java)
                    startActivity(intent)
                }
            }
            // Close the drawer
            binding.drawer.closeDrawers()
            true
        }
    }


    private fun loadRecycelView() {

        binding.recyclerViewAssignedTasks.adapter = CustomAdapter(a, this)
        doneLoading()
    }

    private fun doneLoading() {
        ProgressDialog.dismiss()
        binding.taskTV.visibility = View.VISIBLE

        binding.countdown.visibility = View.VISIBLE
    }

    private fun getAssignedTaskAry() {
        dbref.addValueEventListener(object : ValueEventListener {
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
                    val description =
                        childSnapshot.child("description").getValue(String::class.java)
                    val points = childSnapshot.child("points").getValue(Int::class.java)



                    if (assignedTasksAry.contains(taskId)) {
                        if (taskId != null && title != null && description != null && points != null) {
                            val task = Task(taskId, title, description, points)
                            a.add(task)

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

    override fun onItemClicked(taskId: String, item: Int, item2: View) {

        val taskcomanim = item2.findViewById<LottieAnimationView>(R.id.taskcompletedanim)
        val taskpointll = item2.findViewById<LinearLayout>(R.id.taskpointll)
        val taskbtn = item2.findViewById<AppCompatButton>(R.id.taskcompBtn)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.confirmation_dialog, null)

        val confirmButton = dialogView.findViewById<AppCompatButton>(R.id.dialog_confirm)
        val cancelButton = dialogView.findViewById<AppCompatButton>(R.id.dialog_cancel)


        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()

        dialog.show()


        confirmButton.setOnClickListener {
            binding.availPoints.text = " "
            binding.coinloadprogressbar.visibility = View.VISIBLE
            taskcomanim.visibility = View.VISIBLE


            // Handle the confirmation action here

            // You can call a function or perform any other action
            // when the user confirms.
            databaseReference.child("badge").child(taskId).setValue(true)
            databaseReference.child("assignedTasks").child(taskId).setValue(true)
            databaseReference.child("totalPoints").setValue(currentPoints + item)

            //databaseReference.child("badge").child(Taskid).setValue(true)
            loadpoints()
            //cardLayout.setBackgroundColor(resources.getColor(R.color.black))
            taskbtn.visibility = View.GONE
            taskpointll.visibility = View.GONE
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            // Handle the cancel action here or simply dismiss the dialog.
            dialog.dismiss()
        }


    }

    override fun onResume() {
        loadpoints()
        super.onResume()
    }


}