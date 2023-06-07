package com.youthworkhub.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.youthworkhub.R
import com.youthworkhub.databinding.FragmentCreateJobBinding
import com.youthworkhub.model.CreateJobModel
import com.youthworkhub.ui.activity.MainActivity
import com.youthworkhub.utils.Constants
import com.youthworkhub.utils.PreferencesManager
import com.youthworkhub.viewmodel.MainViewModel
import java.net.URL

class CreateJobFragment : Fragment() {

    private var _binding: FragmentCreateJobBinding? = null
    private val binding get() = _binding!!
    var imageUri: Uri? = null
    private lateinit var mainViewModel: MainViewModel

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

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupButtonClicks()
    }

    private fun setupButtonClicks() {
        binding.postJob.setOnClickListener {
            if(imageUri != null){
                val sd = getFileName(imageUri!!)
                val storageRef = Firebase.storage.reference;
                val ref = storageRef.child("img/$sd")
                val uploadTask = ref.putFile(imageUri!!)

                uploadTask.addOnSuccessListener{
                    Log.i("Firebase", "Upload complited")
                    ref.downloadUrl.addOnSuccessListener {
                        // Got the download URL for 'users/me/profile.png'
                        Log.i("Firebase", "download comp ${it.toString()}")
                        saveJobInDb(it.toString())
                    }.addOnFailureListener {
                        // Handle any errors
                    }


                }.addOnFailureListener {
                    Log.i("Firebase", "Image Upload fail")
                }
            }else{
                saveJobInDb(null)
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
                imageUri = result.data?.data
                if (imageUri != null) {
                    Glide.with(requireContext())
                        .load(imageUri)
                        .into(binding.image)
                }
            }
        }

    private fun getFileName(uri: Uri): String? {
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun saveJobInDb(image: String?) {
        val user = PreferencesManager.getUser()

        if (user != null) {
            val path =
                Firebase.firestore.collection("users").document(user.id)
            val createJobData = CreateJobModel(
                title = binding.titleInput.text.toString(),
                description = binding.decriptionInput.text.toString(),
                skills = binding.skillsInput.text.toString(),
                timestamp = System.currentTimeMillis(),
                owner = path,
                price = binding.priceInput.text.toString(),
                location = binding.locationInput.text.toString(),
                image = image
            )

            Firebase.firestore.collection("job-posts").add(createJobData)
                .addOnSuccessListener { documentReference ->
                    Log.i(
                        "CreateJobTag",
                        "DocumentSnapshot written with ID: ${documentReference.id}"
                    )

                    Toast.makeText(
                        requireContext(),
                        R.string.success_job_created,
                        Toast.LENGTH_SHORT
                    ).show()

                    mainViewModel.switchFragment(Constants.HOME_FRAGMENT)

                }
                .addOnFailureListener { e ->
                    Log.e("CreateJobTag", "Error adding document", e)
                }
        }
    }
}