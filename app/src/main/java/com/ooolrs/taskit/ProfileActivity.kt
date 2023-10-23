package com.ooolrs.taskit
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ooolrs.taskit.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val user = FirebaseAuth.getInstance().currentUser
        databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(user!!.uid)

        // Read user data from Firebase Realtime Database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val points = snapshot.child("totalPoints").value

                    // Update the UI with the user's information
                    updateUI(name, email, points)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur
            }
        })
    }

    private fun updateUI(name: String, email: String, points: Any?) {
        // Update the views in your layout with the user's information
        binding.namePrf.text = name
        binding.emailPrf.text = email
        binding.pointPrf.text = points.toString()
    }


        // Load and display the profile image using a library like Picasso or Glide
        // Example with Picasso:
        // Picasso.get().load(profileImage).into(profileImageView)
    }

