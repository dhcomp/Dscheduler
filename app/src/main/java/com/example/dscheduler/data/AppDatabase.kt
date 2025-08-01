package com.example.dscheduler.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dscheduler.data.model.ActivityRecord

@Database(entities = [ActivityRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityRecordDao(): ActivityRecordDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dscheduler_db"
                ).build().also { instance = it }
            }
    }
}
