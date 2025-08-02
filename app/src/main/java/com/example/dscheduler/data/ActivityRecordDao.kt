package com.example.dscheduler.data

import androidx.room.*
import com.example.dscheduler.data.model.ActivityRecord

@Dao
interface ActivityRecordDao {
    @Insert
    suspend fun insert(record: ActivityRecord)

    @Query("SELECT * FROM activity_record WHERE startTime BETWEEN :startOfDay AND :endOfDay ORDER BY startTime ASC")
    suspend fun getTodayRecords(startOfDay: Long, endOfDay: Long): List<ActivityRecord>

    // 장르별 통계를 위한 새로운 쿼리들
    @Query("""
        SELECT genre, 
               SUM(endTime - startTime) as totalTime 
        FROM activity_record 
        WHERE startTime BETWEEN :startTime AND :endTime 
        GROUP BY genre 
        ORDER BY totalTime DESC
    """)
    suspend fun getGenreStats(startTime: Long, endTime: Long): List<GenreStats>

    @Query("""
        SELECT genre, 
               COUNT(*) as count,
               SUM(endTime - startTime) as totalTime 
        FROM activity_record 
        WHERE startTime BETWEEN :startTime AND :endTime 
        GROUP BY genre 
        ORDER BY totalTime DESC
    """)
    suspend fun getGenreStatsWithCount(startTime: Long, endTime: Long): List<GenreStatsWithCount>
}

// 장르별 통계 데이터 클래스
data class GenreStats(
    val genre: String,
    val totalTime: Long
)

// 장르별 통계 데이터 클래스 (개수 포함)
data class GenreStatsWithCount(
    val genre: String,
    val count: Int,
    val totalTime: Long
)
