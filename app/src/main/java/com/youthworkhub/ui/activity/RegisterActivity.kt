package com.youthworkhub.ui.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()


        }
        auth = Firebase.auth
        binding.signUpButton.setOnClickListener { register() }

    }

    private fun register() {
//        val path = Firebase.firestore.collection("users").document("C67cq8CmVeMbq5SWIStORokq1ze2")
//        Firebase.firestore.collection("job-posts").whereEqualTo("owner", path ).get().addOnSuccessListener { document ->
//            if (document != null) {
//                Log.d("pece", "DocumentSnapshot data: ${document.size()}")
//                for (doc in document)
//                    Log.d("pece", "DocumentSnapshot data: ${doc.data}")
//            } else {
//                Log.d("pece", "No such document")
//            }
//        } .addOnFailureListener { exception ->
//            Log.w("pece", "Error getting documents: ", exception)
//        }
        auth.createUserWithEmailAndPassword(
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if (auth.currentUser != null) {
                    Log.i("REGISTER", "UID" + auth.currentUser!!.uid)
                    val user = auth.currentUser!!

                    val userData = hashMapOf(
                        "name" to binding.usernameInput.text.toString(),
                        "email" to user.email,
                    )
                    Firebase.firestore.collection("users").document(auth.currentUser!!.uid)
                        .set(userData)
                        .addOnCompleteListener(this) { it ->
                            if (it.isSuccessful) {
                                Log.i("TEST", "ZAcuvan")
                            } else {
                                Log.i("TEST", "error")
                            }
                        }

                }
            }
        }
    }
}