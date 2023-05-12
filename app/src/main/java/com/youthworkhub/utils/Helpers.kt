package com.youthworkhub.utils

import com.youthworkhub.model.JobsModel
import com.youthworkhub.model.UserModel
import com.youthworkhub.room.SavedJob

object Helpers {
    fun parseFbUserToUserModal(fbResult: MutableMap<String, Any>?): UserModel? {
        if (fbResult.isNullOrEmpty()) {
            return null
        } else {
            return UserModel(
                fbResult["id"].toString(),
                fbResult["email"].toString(),
                fbResult["username"].toString()
            )
        }
    }

    fun convertToSavedJob(item: JobsModel): SavedJob {
        return SavedJob(
            item.id,
            item.description,
            item.location,
            item.timestamp,
            item.title,
            item.price,
            item.skills,
            item.image
        )
    }

    fun convertToJobModel(item: SavedJob): JobsModel {
        return JobsModel(
            item.id,
            item.description,
            item.location,
            null,
            item.timestamp,
            item.title,
            item.price,
            item.skills,
            item.image,
            true
        )
    }

    fun parseTime(time: Long?): String{
        if (time == null)
            return "N/A"


        val currentTime = System.currentTimeMillis()
        val calcTime = currentTime - time

        return if(calcTime < 600000){ //        od 0 do 1min
            "Just now"
        }else if(calcTime < 3600000){ //les then 60min 1h
            "${calcTime / 60000} min ago" // minutes ago
        }else if(calcTime < 86400000 ){ //less then 24h
            "${calcTime / 3600000} hour ago" // hour ago
        }else if(calcTime < 172800000 ){ // less then 2 days
            "${calcTime / 86400000} day ago" // minutes ago
        }else{ //2 or more days
            "${calcTime / 86400000} days ago" // minutes ago
        }
    }
}