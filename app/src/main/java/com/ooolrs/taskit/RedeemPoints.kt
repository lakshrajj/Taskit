package com.ooolrs.taskit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ooolrs.taskit.databinding.ActivityRedeemPointsBinding

class RedeemPoints : AppCompatActivity() {
    private lateinit var binding: ActivityRedeemPointsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedeemPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rewardsList = resources.getStringArray(R.array.Rewards)

        if (binding.spinner != null) {
            val adapter = ArrayAdapter(this,
                R.layout.spinner_list, rewardsList)
            binding.spinner.adapter = adapter}

        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                //Toast.makeText(this@RedeemPoints, getString(R.string.selected_item)rewardsList[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }}



        auth = FirebaseAuth.getInstance()
        val userid = auth.currentUser?.uid.toString()
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("users").child(userid)


        loadPoints()
    }

    private fun loadPoints() {
        databaseReference.get().addOnSuccessListener {
            if(it.exists()) {
                val currentpoints = it.child("totalPoints").value
                binding.coinloadprogressbar.visibility = View.GONE
                binding.availPoints.text = " "+currentpoints.toString().toInt()
            }
        }
    }
}