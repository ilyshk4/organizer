package com.example.organizer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class TaskEditActivity : AppCompatActivity() {

    lateinit var titleEditText: EditText
    lateinit var descriptionEditText: EditText
    lateinit var dateButton: Button
    lateinit var saveButton: Button

    lateinit var date: LocalDate

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_edit)

        titleEditText  = findViewById(R.id.titleEditText)
        dateButton = findViewById(R.id.dateButton)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveButton = findViewById(R.id.saveButton)

        val day = intent.getLongExtra("day", 0)
        val dayDate = LocalDate.ofEpochDay(day)
        setTaskDate(day)

        dateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                setTaskDate(LocalDate.of(year, month + 1, day).toEpochDay())
            }, dayDate.year, dayDate.monthValue - 1, dayDate.dayOfMonth);
            datePickerDialog.show()
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            if (title.isEmpty()) {
                Toast.makeText(this,"Title is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            TaskDbHelper.instance.insertTask(Task(title, date, description))

            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setTaskDate(day: Long) {
        this.date = LocalDate.ofEpochDay(day)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")
        dateButton.text = date.format(formatter)
    }
}