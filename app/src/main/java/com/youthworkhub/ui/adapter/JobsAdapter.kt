package com.youthworkhub.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.youthworkhub.R
import com.youthworkhub.databinding.JobItemLayoutBinding
import com.youthworkhub.model.JobsModel
import com.youthworkhub.utils.Helpers

class JobsAdapter(
    jobsData: MutableList<JobsModel>,
    val glide: RequestManager,
    var onSaveClick: (JobsModel) -> Unit,
) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    var data: MutableList<JobsModel> = jobsData

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
        holder.binding.jobItemTvTime.text = Helpers.parseTime(current.timestamp)

        holder.binding.jobItemIvSave.setOnClickListener {
            if(current.saved){
                holder.binding.jobItemIvSave.setImageResource(R.drawable.baseline_bookmark_border_24)
            }else{
                holder.binding.jobItemIvSave.setImageResource(R.drawable.baseline_bookmark_24)
            }
            onSaveClick(current)
        }
    }

    fun removeItem(item: JobsModel){
        val index = data.indexOf(item)
        if(index != -1){
            data.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}