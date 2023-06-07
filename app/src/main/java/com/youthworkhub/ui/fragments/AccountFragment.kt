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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.R
import com.youthworkhub.application.AppController
import com.youthworkhub.databinding.FragmentAccountsBinding
import com.youthworkhub.model.JobsModel
import com.youthworkhub.model.UserModel
import com.youthworkhub.ui.adapter.JobsAdapter
import com.youthworkhub.utils.Helpers
import com.youthworkhub.utils.PreferencesManager

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClicks()
        setupUserInfo()
        getJobs()
    }

    private fun setupUserInfo() {
        val user = PreferencesManager.getUser()
        if (user != null) {
            if (!user.image.isNullOrEmpty()) {
                Glide.with(requireContext()).load(user.image).into(binding.accountIv)
            }

            if (!user.firstname.isNullOrEmpty()) {
                binding.accountFirstName.setText(user.firstname)
            }

            if (!user.lastname.isNullOrEmpty()) {
                binding.accountLastName.setText(user.lastname)
            }

            if (user.username.isNotEmpty()) {
                binding.accountUsername.setText(user.username)
            }
        }
    }

    private fun setupClicks() {
        binding.accountIv.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            profileImagePickerActivityResult.launch(galleryIntent)
        }

        binding.accountSaveBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        if (binding.accountFirstName.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), R.string.name_error, Toast.LENGTH_SHORT).show()
        } else if (binding.accountLastName.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), R.string.surname_error, Toast.LENGTH_SHORT).show()
        } else if (binding.accountUsername.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), R.string.username_error, Toast.LENGTH_SHORT).show()
        } else {
            val usr = PreferencesManager.getUser()

            var img: String? = null
            if (imageUri != null) {
                img = imageUri.toString()
            }

            //todo save img

            val updateUserData: MutableMap<String, Any> = mutableMapOf()
            updateUserData["username"] = binding.accountUsername.text.toString()
            updateUserData["firstname"] = binding.accountFirstName.text.toString()
            updateUserData["lastname"] = binding.accountLastName.text.toString()

            if (usr != null) {

                Firebase.firestore.collection("users").document(usr.id).update(updateUserData)
                    .addOnSuccessListener {
                        Log.d("UPDATE", "DocumentSnapshot successfully written!")

                        val user = UserModel(
                            usr.id,
                            usr.email,
                            binding.accountUsername.text.toString(),
                            binding.accountFirstName.text.toString(),
                            binding.accountLastName.text.toString(),
                        )
                        PreferencesManager.putUser(user)

                        Toast.makeText(
                            requireContext(),
                            R.string.update_success,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w("UPDATE", "Error writing document", e)
                        Toast.makeText(requireContext(), R.string.update_failed, Toast.LENGTH_SHORT)
                            .show()
                    }

            }
        }
    }

    private var profileImagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                imageUri = result.data?.data
                if (imageUri != null) {
                    Glide.with(requireContext())
                        .load(imageUri)
                        .into(binding.accountIv)
                }
            }
        }

    private fun getJobs() {
        val savedJobsIds = AppController.roomDb.savedJobDao().getIds()
        val jobsList: MutableList<JobsModel> = mutableListOf()
        val user = PreferencesManager.getUser()

        if (user != null) {
            val path = Firebase.firestore.collection("users").document(user.id)

            Firebase.firestore.collection("job-posts").whereEqualTo("owner", path).get()
                .addOnSuccessListener { jobsData ->
                    if (jobsData != null) {
                        Log.d("AccountFragment", "jobsData: ${jobsData.size()}")

                        jobsData.forEachIndexed { index, jobData ->
                            val objData = JobsModel(
                                id = jobData.id,
                                location = jobData.data["location"].toString(),
                                skills = jobData.data["skills"].toString(),
                                title = jobData.data["title"].toString(),
                                price = jobData.data["price"].toString(),
                                timestamp = jobData.data["timestamp"] as Long?,
                                description = jobData.data["description"].toString(),
                                owner = null,
                                image = jobData.data["image"].toString(),
                                saved = savedJobsIds.contains(jobData.id)
                            )
                            Log.d("AccountFragment", "${jobData.id} => $objData")
                            jobsList.add(objData)

                            if (index == jobsData.size() - 1) {
                                populatePosts(jobsList)
                            }
                        }

                    } else {
                        Log.d("AccountFragment", "jobs are null")
                    }
                }

                .addOnFailureListener { exception ->
                    Log.w("AccountFragment", "Error getting jobs: ", exception)
                }
        }

    }

    private fun populatePosts(jobsList: MutableList<JobsModel>) {
        val jobsAdapter = JobsAdapter(
            jobsList,
            glide = Glide.with(this),
            onSaveClick = { item -> onSaveJob(item) }
        )

        binding.accountPostsRv.adapter = jobsAdapter
        binding.accountPostsRv.layoutManager = LinearLayoutManager(context)
    }

    private fun onSaveJob(item: JobsModel) {
        val saveJobDao = AppController.roomDb.savedJobDao()
        if (item.saved) {
            Log.i("AccountFragment", "deleting")
            saveJobDao.deleteJob(item.id)
        } else {
            Log.i("AccountFragment", "saving")
            val saveData = Helpers.convertToSavedJob(item)
            saveJobDao.saveJob(saveData)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}