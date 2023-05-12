package com.youthworkhub.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SavedJobDao{
    @Query("SELECT * FROM savedjob")
    fun getAll(): List<SavedJob>

    @Query("SELECT id FROM savedjob")
    fun getIds(): List<String>

    @Insert
    fun saveJob(vararg: SavedJob)

    @Query("DELETE FROM savedjob WHERE id = :jobId")
    fun deleteJob(jobId: String)
}