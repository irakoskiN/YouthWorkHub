package com.youthworkhub.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SavedJob(
    @PrimaryKey val id: String,
    @ColumnInfo var description: String?,
    @ColumnInfo var location: String?,
    @ColumnInfo var timestamp: Long?,
    @ColumnInfo var title: String?,
    @ColumnInfo var price: String?,
    @ColumnInfo var skills: String?,
    @ColumnInfo var image: String? = null

)