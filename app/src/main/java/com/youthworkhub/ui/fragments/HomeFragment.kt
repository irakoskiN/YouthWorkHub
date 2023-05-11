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
import com.youthworkhub.JobsAdapter
import com.youthworkhub.R
import com.youthworkhub.databinding.FragmentHomeBinding
import com.youthworkhub.databinding.FragmentWelcomeFirstBinding
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
        val path = Firebase.firestore.collection("users").document("C67cq8CmVeMbq5SWIStORokq1ze2")
        Firebase.firestore.collection("job-posts").whereEqualTo("owner", path ).get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d("pece", "DocumentSnapshot data: ${document.size()}")
                for (doc in document){
//
                    val owner = doc.data.get("owner")as DocumentReference
                    owner.get().addOnSuccessListener { document ->
                        if(document != null){
                            Log.d("pece Owner", "${document.id} => ${document.data}")
                            var ownerObj: UserModel = UserModel(
                                id = document.id,
                                email = document.data?.get("email") as String,
                                username = document.data?.get("username") as String,
                            )
                            var objData: JobsModel = JobsModel(
                                id = doc.id,
                                location = doc.data.get("location").toString(),
                                skills = doc.data.get("skills") as List<String>,
                                title = doc.data.get("title").toString(),
                                price = doc.data.get("price").toString(),
                                timestamp = doc.data.get("timestamp") as Long?,
                                description = doc.data.get("description").toString(),
                                owner = ownerObj,
                                image = null
                            )
                            Log.d("pece Full", "${document.id} => ${objData}")
                            data.add(objData)
                        }
                    }
                        .addOnFailureListener { exception ->
                            Log.w("pece", "Error getting documents: ", exception)
                        }



                }
                setContentAdapter(data)

            } else {
                Log.d("pece", "No such document")
            }
        } .addOnFailureListener { exception ->
            Log.w("pece", "Error getting documents: ", exception)
        }
    }

    private fun setContentAdapter(data: MutableList<JobsModel>) {

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