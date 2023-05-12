package com.youthworkhub.ui.activity

import android.content.Intent
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
import com.youthworkhub.model.UserModel
import com.youthworkhub.utils.Helpers
import com.youthworkhub.utils.PreferencesManager

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
        Log.i("REGISTER", "currUsr ${auth.currentUser}")
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
//                if (auth.currentUser != null) {
                    Log.i("REGISTER", "currUsr ${auth.currentUser}")
                    val user = auth.currentUser!!

                    val userData = UserModel(
                        auth.currentUser!!.uid,
                        user.email.toString(),
                        binding.usernameInput.text.toString(),
                    )
                    Firebase.firestore.collection("users").document(auth.currentUser!!.uid)
                        .set(userData)
                        .addOnCompleteListener(this) { it ->
                            if (it.isSuccessful) {
                                Log.i("FB", "succ saving user")
                                PreferencesManager.putUser(userData)
                                Log.i("FB", "succ saving user ${PreferencesManager.getUser()}")
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.i("FB", "error saving user")
                            }
                        }

//                }else{

//                }
            }
        }
    }
}