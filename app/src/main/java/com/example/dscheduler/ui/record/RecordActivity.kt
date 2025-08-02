package com.example.dscheduler.ui.record

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dscheduler.R
import com.example.dscheduler.data.AppDatabase
import com.example.dscheduler.data.model.ActivityRecord
import kotlinx.coroutines.launch
import java.util.*

class RecordActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var genreSpinner: Spinner
    private lateinit var startDateBtn: Button
    private lateinit var endDateBtn: Button
    private lateinit var startTimeBtn: Button
    private lateinit var endTimeBtn: Button
    private lateinit var saveBtn: Button

    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()
    private var startTime: Calendar = Calendar.getInstance()
    private var endTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        titleEditText = findViewById(R.id.editTitle)
        genreSpinner = findViewById(R.id.spinnerGenre)
        startDateBtn = findViewById(R.id.btnStartDate)
        endDateBtn = findViewById(R.id.btnEndDate)
        startTimeBtn = findViewById(R.id.btnStartTime)
        endTimeBtn = findViewById(R.id.btnEndTime)
        saveBtn = findViewById(R.id.btnSave)

        genreSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.genres,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // 날짜 선택 버튼 설정
        startDateBtn.setOnClickListener { showDatePicker(true) }
        endDateBtn.setOnClickListener { showDatePicker(false) }

        // 시간 선택 버튼 설정
        startTimeBtn.setOnClickListener { showTimePicker(true) }
        endTimeBtn.setOnClickListener { showTimePicker(false) }

        saveBtn.setOnClickListener {
            val title = titleEditText.text.toString()
            val genre = genreSpinner.selectedItem.toString()

            if (title.isBlank()) {
                Toast.makeText(this, "활동 제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 시작 시간과 종료 시간을 결합
            val startDateTime = Calendar.getInstance().apply {
                set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, startTime.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val endDateTime = Calendar.getInstance().apply {
                set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, endTime.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val record = ActivityRecord(
                title = title,
                genre = genre,
                startTime = startDateTime.timeInMillis,
                endTime = endDateTime.timeInMillis
            )

            lifecycleScope.launch {
                AppDatabase.getDatabase(applicationContext).activityRecordDao().insert(record)
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun showDatePicker(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        
        DatePickerDialog(this, { _, year, month, day ->
            if (isStart) {
                startDate.set(Calendar.YEAR, year)
                startDate.set(Calendar.MONTH, month)
                startDate.set(Calendar.DAY_OF_MONTH, day)
                startDateBtn.text = "시작: ${year}-${month + 1}-${day}"
            } else {
                endDate.set(Calendar.YEAR, year)
                endDate.set(Calendar.MONTH, month)
                endDate.set(Calendar.DAY_OF_MONTH, day)
                endDateBtn.text = "종료: ${year}-${month + 1}-${day}"
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        
        TimePickerDialog(this, { _, hour, minute ->
            if (isStart) {
                startTime.set(Calendar.HOUR_OF_DAY, hour)
                startTime.set(Calendar.MINUTE, minute)
                startTimeBtn.text = "시작: ${String.format("%02d:%02d", hour, minute)}"
            } else {
                endTime.set(Calendar.HOUR_OF_DAY, hour)
                endTime.set(Calendar.MINUTE, minute)
                endTimeBtn.text = "종료: ${String.format("%02d:%02d", hour, minute)}"
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }
}
