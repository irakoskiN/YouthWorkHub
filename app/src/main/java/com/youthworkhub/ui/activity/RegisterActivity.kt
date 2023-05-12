package com.youthworkhub.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.databinding.ActivityRegisterBinding
import com.youthworkhub.model.UserModel
import com.youthworkhub.utils.PreferencesManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        Log.i("RegisterTag", "currUsr ${auth.currentUser}")

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.textView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.signUpButton.setOnClickListener {
            register()
        }
    }

    private fun register() {
        auth.createUserWithEmailAndPassword(
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = task.result.user
                if (user != null && user.uid.isNotEmpty()) {
                    Log.i("RegisterTag", "currUsr $user")
                    val userData = UserModel(
                        user.uid,
                        user.email.toString(),
                        binding.usernameInput.text.toString(),
                    )
                    Firebase.firestore.collection("users").document(auth.currentUser!!.uid)
                        .set(userData)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                Log.i("RegisterTag", "success saving user")
                                PreferencesManager.putUser(userData)
                                Log.i("RegisterTag", "success saving user ${PreferencesManager.getUser()}")
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.i("RegisterTag", "error saving user")
                            }
                        }
                }
            }
        }
    }
}