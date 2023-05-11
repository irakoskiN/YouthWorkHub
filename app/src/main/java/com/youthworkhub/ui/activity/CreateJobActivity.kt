package com.youthworkhub.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.databinding.ActivityCreateJobBinding
import com.youthworkhub.databinding.ActivityMainBinding
import com.youthworkhub.model.CreateJobModel
import com.youthworkhub.model.JobsModel
import com.youthworkhub.model.UserModel

class CreateJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateJobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtonClicks()

    }
    fun setupButtonClicks(){
        binding.postJob.setOnClickListener {
            val path = Firebase.firestore.collection("users").document("C67cq8CmVeMbq5SWIStORokq1ze2")
            val createJobData = CreateJobModel(
                title = binding.titleInput.text.toString(),
                description = binding.decriptionInput.text.toString(),
                skills = binding.skillsInput.text.toString(),
                timestamp = System.currentTimeMillis(),
                owner =  path,
                price = binding.priceInput.text.toString(),
                location = binding.locationInput.text.toString()
            )

            Firebase.firestore.collection("job-posts").add(createJobData).addOnSuccessListener { documentReference ->
                Log.d("pece", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
                .addOnFailureListener { e ->
                    Log.w("pece", "Error adding document", e)
                }
        }
    }
}