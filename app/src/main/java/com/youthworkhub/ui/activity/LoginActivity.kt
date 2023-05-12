package com.youthworkhub.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.databinding.ActivityLoginBinding
import com.youthworkhub.utils.Helpers
import com.youthworkhub.utils.PreferencesManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onResume() {
        super.onResume()
        initAuth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtonClicks()
    }

    private fun initAuth() {
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupButtonClicks() {
        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val pass = binding.passwordInput.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                login(email, pass)
            }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i("LoginTAG", "login successful")
                    Firebase.firestore.collection("users").document(task.result.user!!.uid).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d("LoginTAG", "DocumentSnapshot data: ${document.data}")
                                val userData = Helpers.parseFbUserToUserModal(document.data)
                                if (userData != null) {
                                    PreferencesManager.putUser(userData)
                                }

                            } else {
                                Log.d("LoginTAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("LoginTAG", "get failed with ", exception)
                        }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.i("LoginTAG", "login failed", task.exception)
                    // todo show dialog
                }
            }
    }
}