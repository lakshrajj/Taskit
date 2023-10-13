package com.ooolrs.taskit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ooolrs.taskit.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()

        binding.SignupBtn.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val password2 = binding.passwordInput2.text.toString()

        if(password==password2){
        if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                  .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            val email = binding.emailInput.text.toString()
                            val name = binding.nameInput.text.toString()

                            val user = User(userId.toString(), name, email)

                            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                            val usersRef: DatabaseReference = database.getReference("users")

                            usersRef.child(userId.toString()).setValue(user)
                            usersRef.child(userId.toString()).child("totalPoints").setValue(10)

                            for (i in 1..50) {
                                usersRef.child(userId.toString()).child("badge").child("task$i").setValue(false)
                            }



                            Toast.makeText(this, "Account created successfully",
                                Toast.LENGTH_SHORT).show()
                            // Sign in success, update UI with the signed-in user's information
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            // You can navigate to another activity or perform other actions here
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            //...
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }

    }
}}