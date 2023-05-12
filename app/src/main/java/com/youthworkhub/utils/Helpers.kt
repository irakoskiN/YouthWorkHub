package com.youthworkhub.utils

import com.youthworkhub.model.UserModel

object Helpers {
    public fun parseFbUserToUserModal(fbRezult: MutableMap<String, Any>?) :UserModel?{
        if (fbRezult.isNullOrEmpty()){
            return null
        }else{
            return UserModel(
                fbRezult.get("id").toString(),
                fbRezult.get("email").toString(),
                fbRezult.get("username").toString()
            )
        }

    }
}