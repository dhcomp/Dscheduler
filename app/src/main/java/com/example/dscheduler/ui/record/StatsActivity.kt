package com.example.dscheduler.ui.record

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dscheduler.R
import com.example.dscheduler.data.AppDatabase
import com.example.dscheduler.data.GenreStatsWithCount
import kotlinx.coroutines.*
import java.time.*
import java.util.*

class StatsActivity : AppCompatActivity() {

    private lateinit var periodSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatsAdapter
    private lateinit var textTotalTime: TextView
    private lateinit var textPeriod: TextView

    private val db by lazy { AppDatabase.getDatabase(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        periodSpinner = findViewById(R.id.spinnerPeriod)
        recyclerView = findViewById(R.id.recyclerStats)
        textTotalTime = findViewById(R.id.textTotalTime)
        textPeriod = findViewById(R.id.textPeriod)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StatsAdapter()
        recyclerView.adapter = adapter

        // 기간 선택 스피너 설정
        periodSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.periods,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                loadStats(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 초기 통계 로드 (이번 주)
        loadStats(0)
    }

    private fun loadStats(periodIndex: Int) {
        val (startTime, endTime, periodText) = when (periodIndex) {
            0 -> getWeekRange() // 이번 주
            1 -> getMonthRange() // 이번 달
            2 -> getYearRange() // 이번 년
            else -> getWeekRange()
        }

        textPeriod.text = periodText

        CoroutineScope(Dispatchers.Main).launch {
            val stats = withContext(Dispatchers.IO) {
                db.activityRecordDao().getGenreStatsWithCount(startTime, endTime)
            }
            
            adapter.submitList(stats)
            
            // 총 시간 계산 및 표시
            val totalTime = stats.sumOf { it.totalTime }
            val totalHours = totalTime / (1000 * 60 * 60)
            val totalMinutes = (totalTime % (1000 * 60 * 60)) / (1000 * 60)
            textTotalTime.text = "총 시간: ${totalHours}시간 ${totalMinutes}분"
        }
    }

    private fun getWeekRange(): Triple<Long, Long, String> {
        val now = LocalDate.now()
        val startOfWeek = now.with(java.time.DayOfWeek.MONDAY)
        val endOfWeek = startOfWeek.plusDays(6)
        
        val startMillis = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endOfWeek.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return Triple(startMillis, endMillis, "이번 주 (${startOfWeek.monthValue}/${startOfWeek.dayOfMonth} ~ ${endOfWeek.monthValue}/${endOfWeek.dayOfMonth})")
    }

    private fun getMonthRange(): Triple<Long, Long, String> {
        val now = LocalDate.now()
        val startOfMonth = now.withDayOfMonth(1)
        val endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
        
        val startMillis = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endOfMonth.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return Triple(startMillis, endMillis, "이번 달 (${now.year}년 ${now.monthValue}월)")
    }

    private fun getYearRange(): Triple<Long, Long, String> {
        val now = LocalDate.now()
        val startOfYear = now.withDayOfYear(1)
        val endOfYear = now.withDayOfYear(now.lengthOfYear())
        
        val startMillis = startOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endOfYear.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return Triple(startMillis, endMillis, "이번 년 (${now.year}년)")
    }
} 