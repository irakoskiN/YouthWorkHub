package com.youthworkhub.application

import android.app.Application
import android.content.SharedPreferences
import com.youthworkhub.utils.Constants

class AppController : Application() {

    companion object {
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, MODE_PRIVATE)
    }
}