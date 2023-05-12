package com.youthworkhub.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.youthworkhub.R
import com.youthworkhub.adapter.JobsAdapter
import com.youthworkhub.application.AppController
import com.youthworkhub.databinding.FragmentHomeBinding
import com.youthworkhub.databinding.FragmentSavedBinding
import com.youthworkhub.model.JobsModel
import com.youthworkhub.room.SavedJob

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

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
//        TODO call room DB
        val savedObj =  AppController.roomDb.savedJobDao().getAll()
        Log.d("ROOM", "the data:  ${savedObj} " )
        val arrOfSavedJob:MutableList<JobsModel> = mutableListOf()
        for (item in savedObj){
            val saveData = JobsModel(
                item.id,
                item.description,
                item.location,
                null,
                item.timestamp,
                item.title,
                item.price,
                item.skills,
                item.image
            )
            arrOfSavedJob.add(saveData)
        }

        setContentAdapter(arrOfSavedJob)
    }

    private fun setContentAdapter(data: MutableList<JobsModel>) {

        val jobsAdapter = JobsAdapter(
            data,
            glide = Glide.with(this),
            onSaveClick = {}
        )

        binding.savedRv.adapter = jobsAdapter
        binding.savedRv.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}