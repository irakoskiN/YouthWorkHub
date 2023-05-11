package com.youthworkhub.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.youthworkhub.R
import com.youthworkhub.databinding.JobItemLayoutBinding
import com.youthworkhub.model.JobsModel

class JobsAdapter(
    val data: MutableList<JobsModel>,
    val glide: RequestManager,
) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: JobItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = JobItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = data[position]

        Log.i("pece", "curent "+current.title)

        if (!current.image.isNullOrEmpty()) {
            glide
                .load(current.image)
                .into(holder.binding.jobItemIv)
        } else {
            holder.binding.jobItemIv.setImageResource(R.drawable.dog_walking)
        }

        holder.binding.jobItemTvTitle.text = current.title
        holder.binding.jobItemTvDesc.text = current.description
        holder.binding.jobItemTvSkills.text = current.skills
        holder.binding.jobItemTvTime.text = current.timestamp.toString()
    }

    override fun getItemCount(): Int {
       return data.size
    }
}