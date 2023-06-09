package com.youthworkhub.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.youthworkhub.application.AppController
import com.youthworkhub.model.UserModel

object PreferencesManager {

    fun putPreviouslyOpened(opened: Boolean) {
        AppController.sharedPreferences.edit().putBoolean(Constants.APP_PREVIOUSLY_OPENED, opened).apply()
    }

    fun getPreviouslyOpened(): Boolean {
        return AppController.sharedPreferences.getBoolean(Constants.APP_PREVIOUSLY_OPENED, false)
    }

    fun putUser(user: UserModel) {
        AppController.sharedPreferences.edit().putString(Constants.USER_MODEL, Gson().toJson(user)).apply()
    }

    fun getUser(): UserModel? {
        return Gson().fromJson(
            AppController.sharedPreferences.getString(Constants.USER_MODEL, ""),
            object : TypeToken<UserModel>() {
            }.type
        )
    }

    fun logout() {
        AppController.sharedPreferences.edit().remove(Constants.USER_MODEL).apply()
    }
}