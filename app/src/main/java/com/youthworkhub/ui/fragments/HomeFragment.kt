package com.youthworkhub.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.adapter.JobsAdapter
import com.youthworkhub.databinding.FragmentHomeBinding
import com.youthworkhub.model.JobsModel
import com.youthworkhub.model.UserModel

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

        var data: MutableList<JobsModel> = mutableListOf()
//        TODO chage doc path to userId
        val path = Firebase.firestore.collection("users").document("C67cq8CmVeMbq5SWIStORokq1ze2")
        Firebase.firestore.collection("job-posts").whereEqualTo("owner", path).get()
            .addOnSuccessListener { jobsData ->

                if (jobsData != null) {
                    Log.d("pece", "DocumentSnapshot data: ${jobsData.size()}")
                    jobsData.forEachIndexed { index, jobData ->
                        var objData: JobsModel = JobsModel(
                            id = jobData.id,
                            location = jobData.data.get("location").toString(),
                            skills = jobData.data.get("skills").toString(),
                            title = jobData.data.get("title").toString(),
                            price = jobData.data.get("price").toString(),
                            timestamp = jobData.data.get("timestamp") as Long?,
                            description = jobData.data.get("description").toString(),
                            owner = null,
                            image =  jobData.data.get("image").toString()
                        )
                        Log.d("pece Full", "${jobData.id} => ${objData}")
                        data.add(objData)

                        if(index ==  jobsData.size() - 1){
                            setContentAdapter(data)
                        }
                    }
//                    for (jobData in jobsData) {
//
//                        // Owner may not be need in this fragment only in details screen
//                        val owner = jobData.data.get("owner") as DocumentReference
//                        owner.get().addOnSuccessListener { ownerData ->
//                            if (ownerData != null) {
//                                Log.d("pece Owner", "${ownerData.id} => ${ownerData.data}")
//                                var ownerObj: UserModel = UserModel(
//                                    id = ownerData.id,
//                                    email = ownerData.data?.get("email") as String,
//                                    username = ownerData.data?.get("username") as String,
//                                )
//                                var objData: JobsModel = JobsModel(
//                                    id = jobData.id,
//                                    location = jobData.data.get("location").toString(),
//                                    skills = jobData.data.get("skills").toString(),
//                                    title = jobData.data.get("title").toString(),
//                                    price = jobData.data.get("price").toString(),
//                                    timestamp = jobData.data.get("timestamp") as Long?,
//                                    description = jobData.data.get("description").toString(),
//                                    owner = ownerObj,
//                                    image = null
//                                )
//                                Log.d("pece Full", "${jobData.id} => ${objData}")
//                                data.add(objData)
//
//                            }
//                        }
//                            .addOnFailureListener { exception ->
//                                Log.w("pece", "Error getting documents: ", exception)
//                            }
//
//
//                    }
//                    setContentAdapter(data)
                } else {
                    Log.d("pece", "No such document")
                }
            }.addOnFailureListener { exception ->
            Log.w("pece", "Error getting documents: ", exception)
        }
    }

    private fun setContentAdapter(data: MutableList<JobsModel>) {

        Log.d("pece", "setContentAdapter " + data.size)

        val jobsAdapter = JobsAdapter(
            data,
            glide = Glide.with(this),
        )

        binding.jobsRv.adapter = jobsAdapter
        binding.jobsRv.layoutManager = LinearLayoutManager(context)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}