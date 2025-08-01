package com.example.dscheduler.ui.record

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dscheduler.R
import com.example.dscheduler.data.AppDatabase
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.*
import java.time.*
import android.view.View
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordAdapter
    private lateinit var textCurrentMonth: TextView

    private val db by lazy { AppDatabase.getDatabase(applicationContext) }

    private val recordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        selectedDate?.let { loadRecordsForDate(it) }
    }

    private var selectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerRecords)
        textCurrentMonth = findViewById(R.id.textCurrentMonth)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecordAdapter()
        recyclerView.adapter = adapter

        val today = LocalDate.now()
        val firstMonth = YearMonth.from(today)
        val lastMonth = firstMonth.plusMonths(12)

        calendarView.setup(firstMonth, lastMonth, DayOfWeek.SUNDAY)
        calendarView.scrollToMonth(firstMonth)

        // 년월 텍스트 초기화 및 갱신 리스너 연결
        updateMonthTitle(firstMonth)
        calendarView.monthScrollListener = { month ->
            updateMonthTitle(month.yearMonth)
        }

        // day binder
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.bind(data)
            }
        }

        selectedDate = today
        loadRecordsForDate(today)

        findViewById<Button>(R.id.btnAddRecord).setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            recordLauncher.launch(intent)
        }
    }

    private fun updateMonthTitle(yearMonth: YearMonth) {
        textCurrentMonth.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
    }

    private fun loadRecordsForDate(date: LocalDate) {
        selectedDate = date

        val startMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        CoroutineScope(Dispatchers.Main).launch {
            val records = withContext(Dispatchers.IO) {
                db.activityRecordDao().getTodayRecords(startMillis, endMillis)
            }
            adapter.submitList(records)
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val textView: TextView = view.findViewById(R.id.calendarDayText)

        init {
            view.setOnClickListener {
                val day = view.tag as? CalendarDay ?: return@setOnClickListener
                loadRecordsForDate(day.date)
            }
        }

        fun bind(day: CalendarDay) {
            view.tag = day
            textView.text = day.date.dayOfMonth.toString()
        }
    }
}
