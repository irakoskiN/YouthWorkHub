package com.youthworkhub.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.ui.adapter.JobsAdapter
import com.youthworkhub.application.AppController
import com.youthworkhub.databinding.FragmentHomeBinding
import com.youthworkhub.model.JobsModel
import com.youthworkhub.utils.Helpers

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getJobs()
    }

    private fun getJobs() {
        val savedJobsIds = AppController.roomDb.savedJobDao().getIds()
        val allJobsList: MutableList<JobsModel> = mutableListOf()

        Firebase.firestore.collection("job-posts").get()
            .addOnSuccessListener { jobsData ->
                if (jobsData != null) {
                    Log.d("HomeFragmentTag", "jobsData: ${jobsData.size()}")

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
                        Log.d("HomeFragmentTag", "${jobData.id} => $objData")
                        allJobsList.add(objData)

                        if (index == jobsData.size() - 1) {
                            setContentAdapter(allJobsList)
                        }
                    }

                } else {
                    Log.d("HomeFragmentTag", "jobs are null")
                }
            }

            .addOnFailureListener { exception ->
                Log.w("HomeFragmentTag", "Error getting jobs: ", exception)
            }
    }

    private fun setContentAdapter(data: MutableList<JobsModel>) {
        val jobsAdapter = JobsAdapter(
            data,
            glide = Glide.with(this),
            onSaveClick = { item -> onSaveJob(item) }
        )

        binding.jobsRv.adapter = jobsAdapter
        binding.jobsRv.layoutManager = LinearLayoutManager(context)
    }

    private fun onSaveJob(item: JobsModel) {
        val saveJobDao = AppController.roomDb.savedJobDao()
        if (item.saved) {
            Log.i("HomeFragmentTag", "deleting")
            saveJobDao.deleteJob(item.id)
        } else {
            Log.i("HomeFragmentTag", "saving")
            val saveData = Helpers.convertToSavedJob(item)
            saveJobDao.saveJob(saveData)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}