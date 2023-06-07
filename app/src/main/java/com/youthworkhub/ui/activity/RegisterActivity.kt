package com.youthworkhub.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.R
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
            validateData()
        }
    }

    private fun validateData() {
        if (binding.usernameInput.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, R.string.username_error, Toast.LENGTH_SHORT).show()
        } else if (binding.emailInput.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, R.string.email_error, Toast.LENGTH_SHORT).show()
        } else if (binding.passwordInput.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, R.string.pass_error, Toast.LENGTH_SHORT).show()
        } else if (binding.passwordInput.text.toString() != binding.confirmPasswordInput.text.toString()) {
            Toast.makeText(applicationContext, R.string.confirm_pass_error, Toast.LENGTH_SHORT).show()
        } else {
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
                                Log.i(
                                    "RegisterTag",
                                    "success saving user ${PreferencesManager.getUser()}"
                                )
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