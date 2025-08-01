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
    private lateinit var startTimeBtn: Button
    private lateinit var endTimeBtn: Button
    private lateinit var saveBtn: Button

    private var startTime: Long = 0
    private var endTime: Long = 0

    private val startCalendar: Calendar = Calendar.getInstance()
    private val endCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        titleEditText = findViewById(R.id.editTitle)
        genreSpinner = findViewById(R.id.spinnerGenre)
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

        startTimeBtn.setOnClickListener { showDateTimePicker(true) }
        endTimeBtn.setOnClickListener { showDateTimePicker(false) }

        saveBtn.setOnClickListener {
            val title = titleEditText.text.toString()
            val genre = genreSpinner.selectedItem.toString()

            if (title.isBlank() || startTime == 0L || endTime == 0L) {
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val record = ActivityRecord(
                title = title,
                genre = genre,
                startTime = startTime,
                endTime = endTime
            )

            lifecycleScope.launch {
                AppDatabase.getDatabase(applicationContext).activityRecordDao().insert(record)
                setResult(RESULT_OK) // ✅ MainActivity로 성공 결과 전달
                finish()
            }
        }
    }

    private fun showDateTimePicker(isStart: Boolean) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            TimePickerDialog(this, { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                val timeMillis = calendar.timeInMillis
                val formatted = "%04d-%02d-%02d %02d:%02d".format(year, month + 1, day, hour, minute)

                if (isStart) {
                    startCalendar.timeInMillis = timeMillis
                    startTime = timeMillis
                    startTimeBtn.text = "시작: $formatted"
                } else {
                    endCalendar.timeInMillis = timeMillis
                    endTime = timeMillis
                    endTimeBtn.text = "종료: $formatted"
                }

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}
