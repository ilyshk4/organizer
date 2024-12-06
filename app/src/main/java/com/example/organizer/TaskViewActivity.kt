package com.example.organizer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.organizer.databinding.ActivityTaskViewBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TaskViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskViewBinding

    lateinit var linearLayout: LinearLayout
    var taskCount: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        linearLayout = findViewById(R.id.linearLayout)
        val dateTextView = findViewById<TextView>(R.id.dateTextView)

        val day = intent.getLongExtra("day", -1)

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")
        dateTextView.text = LocalDate.ofEpochDay(day).format(formatter)

        val tasks = TaskDbHelper.instance.getTasks(day)

        tasks.forEach {
            putTask(it)
        }
    }

    @SuppressLint("InflateParams")
    private fun putTask(task: Task) {
        val item = layoutInflater.inflate(R.layout.task_view_layout, null)

        linearLayout.addView(item)
        taskCount++

        val titleTextView = item.findViewById<TextView>(R.id.titleTextView)
        val descriptionTextView = item.findViewById<TextView>(R.id.descriptionTextView)
        val finishButton = item.findViewById<Button>(R.id.finishButton)

        titleTextView.text = task.title
        descriptionTextView.text = task.description

        finishButton.setOnClickListener {
            TaskDbHelper.instance.finishTask(task.id)
            linearLayout.removeView(item)
            taskCount--
            if (taskCount == 0) {
                finish()
            }
        }
    }
}