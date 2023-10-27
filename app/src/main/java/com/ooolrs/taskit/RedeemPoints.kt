package com.ooolrs.taskit

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.ooolrs.lockward.db.encryption
import com.ooolrs.taskit.databinding.ActivityRedeemPointsBinding
import android.util.Base64

class RedeemPoints : AppCompatActivity() {
    private lateinit var binding: ActivityRedeemPointsBinding
    private lateinit var ProgressDialog: Dialog
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference
    private val encryption = encryption()
    private var rewardClaimed: String? = null
    private var currentPoints: Int? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedeemPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("users").child(userId)
        databaseReference2 = database.getReference("reward")

        loadPoints()

        ProgressDialog = Dialog(this)
        ProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        ProgressDialog.setContentView(R.layout.loading_dialog)
        ProgressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        ProgressDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        ProgressDialog.setCancelable(false)

        val rewardsList = resources.getStringArray(R.array.Rewards)
        val adapter = ArrayAdapter(this, R.layout.spinner_list, rewardsList)
        binding.spinner.adapter = adapter

        binding.rewardBtn.setOnClickListener {
            val selectedItem = binding.spinner.selectedItem.toString()
            if (selectedItem == "Select") {
                return@setOnClickListener
            }

            if (selectedItem == "Duty Leave (1 Lecture)") {
                ProgressDialog.show()
                val timestamp = ServerValue.TIMESTAMP

                if (currentPoints!! > 100) {
                    val transactionID = encryption.encrypt("DL$rewardClaimed", "lakshrajme@gmail.com")

                    val encodedKey = transactionID.replace("/", "11")

                    databaseReference2.child(encodedKey).apply {
                        child("claim").setValue(false)
                        child("date").setValue(timestamp)
                        child("email").setValue(userEmail)
                        child("rewardname").setValue("Duty Leave (1 Lecture)")
                    }

                    databaseReference.apply {
                        child("totalPoints").setValue(currentPoints!! - 100)
                        child("rewardsClaimed").child(encodedKey).setValue(false)
                    }

                    loadPoints()
                } else {
                    Toast.makeText(this, "Not Enough Coins", Toast.LENGTH_SHORT).show()
                }

                ProgressDialog.dismiss()

                return@setOnClickListener
            }
        }
    }

    private fun loadPoints() {
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                currentPoints = dataSnapshot.child("totalPoints").getValue(Int::class.java)
                rewardClaimed = dataSnapshot.child("rewardsClaimed").childrenCount.toString()
                userEmail = dataSnapshot.child("email").getValue(String::class.java)
                binding.coinloadprogressbar.visibility = View.GONE
                binding.availPoints.text = " " + currentPoints.toString()
            }
        }
    }
}
