package com.youthworkhub.adapter

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
    var onSaveClick: (JobsModel) -> Unit,
) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: JobItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = JobItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = data[position]

        if(current.saved){
            holder.binding.jobItemIvSave.setImageResource(R.drawable.baseline_bookmark_24)
        }
        if (!current.image.isNullOrEmpty() && current.image != "null") {
            glide
                .load(current.image)
                .into(holder.binding.jobItemIv)
            holder.binding.jobItemIv.clipToOutline = true
        } else {
            holder.binding.jobItemIv.setImageResource(R.drawable.dog_walking)
            holder.binding.jobItemIv.clipToOutline = true
        }

        holder.binding.jobItemTvTitle.text = current.title
        holder.binding.jobItemTvDesc.text = current.description
        holder.binding.jobItemTvSkills.text = current.skills
        holder.binding.jobItemTvTime.text = current.timestamp.toString()

        holder.binding.jobItemIvSave.setOnClickListener {
            if(current.saved){
                holder.binding.jobItemIvSave.setImageResource(R.drawable.baseline_bookmark_24)
            }else{
                holder.binding.jobItemIvSave.setImageResource(R.drawable.baseline_bookmark_border_24)
            }
            onSaveClick(current)
        }
    }

    fun setSavedJob(id: String){

    }
    override fun getItemCount(): Int {
        return data.size
    }
}