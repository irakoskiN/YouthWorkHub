package com.youthworkhub.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedJob::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedJobDao(): SavedJobDao
}