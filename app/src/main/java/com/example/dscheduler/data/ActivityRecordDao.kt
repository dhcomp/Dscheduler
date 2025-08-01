package com.example.dscheduler.data

import androidx.room.*
import com.example.dscheduler.data.model.ActivityRecord

@Dao
interface ActivityRecordDao {
    @Insert
    suspend fun insert(record: ActivityRecord)

    @Query("SELECT * FROM activity_record WHERE startTime BETWEEN :startOfDay AND :endOfDay ORDER BY startTime ASC")
    suspend fun getTodayRecords(startOfDay: Long, endOfDay: Long): List<ActivityRecord>
}
