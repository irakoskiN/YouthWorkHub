package com.youthworkhub.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.youthworkhub.ui.adapter.JobsAdapter
import com.youthworkhub.application.AppController
import com.youthworkhub.databinding.FragmentSavedBinding
import com.youthworkhub.model.JobsModel
import com.youthworkhub.utils.Helpers

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private var jobsAdapter: JobsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSavedItems()
    }

    private fun getSavedItems() {
        val savedObj = AppController.roomDb.savedJobDao().getAll()
        Log.d("SavedFragmentTag", "saved data:  $savedObj")

        val arrOfSavedJobs: MutableList<JobsModel> = mutableListOf()
        for (item in savedObj) {
            val saveData = Helpers.convertToJobModel(item)
            arrOfSavedJobs.add(saveData)
        }

        setContentAdapter(arrOfSavedJobs)
    }

    private fun setContentAdapter(data: MutableList<JobsModel>) {
        jobsAdapter = JobsAdapter(
            data,
            glide = Glide.with(this),
            onSaveClick = { item -> removeItem(item) }
        )

        binding.savedRv.adapter = jobsAdapter
        binding.savedRv.layoutManager = LinearLayoutManager(context)
    }

    private fun removeItem(item: JobsModel) {
        AppController.roomDb.savedJobDao().deleteJob(item.id)
        jobsAdapter?.removeItem(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}