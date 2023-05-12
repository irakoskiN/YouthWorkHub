package com.youthworkhub.application

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.youthworkhub.room.AppDatabase
import com.youthworkhub.utils.Constants

class AppController : Application() {

    companion object {
        lateinit var sharedPreferences: SharedPreferences
        lateinit var roomDb: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, MODE_PRIVATE)
        roomDb = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "savedjob"
        ).allowMainThreadQueries().build()
    }
}