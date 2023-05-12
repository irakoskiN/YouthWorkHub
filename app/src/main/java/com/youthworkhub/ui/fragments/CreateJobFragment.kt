package com.youthworkhub.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.R
import com.youthworkhub.databinding.FragmentCreateJobBinding
import com.youthworkhub.model.CreateJobModel
import com.youthworkhub.ui.activity.MainActivity
import com.youthworkhub.utils.PreferencesManager

class CreateJobFragment : Fragment() {

    private var _binding: FragmentCreateJobBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtonClicks()
    }

    private fun setupButtonClicks() {
        binding.postJob.setOnClickListener {
            val path = Firebase.firestore.collection("users").document(PreferencesManager.getUser().id)
            val createJobData = CreateJobModel(
                title = binding.titleInput.text.toString(),
                description = binding.decriptionInput.text.toString(),
                skills = binding.skillsInput.text.toString(),
                timestamp = System.currentTimeMillis(),
                owner = path,
                price = binding.priceInput.text.toString(),
                location = binding.locationInput.text.toString()
            )

            Firebase.firestore.collection("job-posts").add(createJobData)
                .addOnSuccessListener { documentReference ->
                    Log.d("CreateJobTag", "DocumentSnapshot written with ID: ${documentReference.id}")
//                    TODO open home fragment

                }
                .addOnFailureListener { e ->
                    Log.w("CreateJobTag", "Error adding document", e)
                }
        }

        binding.image.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)
        }
    }


    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                val imageUri: Uri? = result.data?.data

                // val fileName = imageUri?.pathSegments?.last()

                // extract the file name with extension
                val sd = getFileName(imageUri!!)
                Glide.with(requireContext())
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

    private fun getFileName(uri: Uri): String? {
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
}