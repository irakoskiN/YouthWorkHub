package com.youthworkhub.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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

        binding.image.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"
            // ActivityResultLauncher callback
            imagePickerActivityResult.launch(galleryIntent)
        }
        setupButtonClicks()

    }
    fun setupButtonClicks(){
        binding.postJob.setOnClickListener {
//            TODO change docpath with userId
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


    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                val imageUri: Uri? = result.data?.data

                // val fileName = imageUri?.pathSegments?.last()

                // extract the file name with extension
                val sd = getFileName(applicationContext, imageUri!!)
                Glide.with(this@CreateJobActivity)
                    .load(imageUri)
                    .into(binding.image)

//                // Upload Task with upload to directory 'file'
//                // and name of the file remains same
//                val storageRef = Firebase.storage.reference;
//                val uploadTask = storageRef.child("img/$sd").putFile(imageUri)
//
//                // On success, download the file URL and display it
//                uploadTask.addOnSuccessListener {
//                    // using glide library to display the image
//                    storageRef.child("img/$sd").downloadUrl.addOnSuccessListener {
//                        Glide.with(this@CreateJobActivity)
//                            .load(it)
//                            .into(binding.image)
//
//                        Log.e("Firebase", "download passed")
//                    }.addOnFailureListener {
//                        Log.e("Firebase", "Failed in downloading")
//                    }
//                }.addOnFailureListener {
//                    Log.e("Firebase", "Image Upload fail")
//                }


            }
        }
    private fun getFileName(context: Context, uri: Uri): String? {
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
}