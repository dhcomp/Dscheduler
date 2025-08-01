package com.example.dscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_record")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val genre: String,
    val startTime: Long,
    val endTime: Long
)
