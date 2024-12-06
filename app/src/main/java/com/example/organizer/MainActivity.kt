package com.example.organizer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var calendarView: CalendarView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TaskDbHelper.instance = TaskDbHelper(this)

        calendarView = findViewById(R.id.calendarView)
        val addTaskButton = findViewById<FloatingActionButton>(R.id.addTaskButton)
        addTaskButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val month = calendarView.findFirstVisibleMonth()!!.yearMonth
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val switchActivityIntent = Intent(this, TaskEditActivity::class.java)
                val epochDay = LocalDate.of(year, month + 1, day).toEpochDay()
                switchActivityIntent.putExtra("day", epochDay);
                startActivity(switchActivityIntent)
            }, calendar[Calendar.YEAR], month.monthValue - 1, calendar[Calendar.DAY_OF_MONTH])
            datePickerDialog.show()
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val count = TaskDbHelper.instance.getTaskCount(data.date.toEpochDay())
                val active = data.position == DayPosition.MonthDate

                val red = ContextCompat.getColor(applicationContext, R.color.red)
                val grey = ContextCompat.getColor(applicationContext, R.color.grey)
                val redDusk = ContextCompat.getColor(applicationContext, R.color.red_dusk)

                container.dayTextView.text = data.date.dayOfMonth.toString()

                container.day = data
                container.taskCount = count

                if (data.date.isEqual(LocalDate.now()))
                {
                    if (active)
                        container.panel.setBackgroundColor(red)
                    else
                        container.panel.setBackgroundColor(redDusk)
                } else {
                    container.panel.setBackgroundColor(grey)
                }

                if (data.date.dayOfWeek.value > 5)
                {
                    if (active)
                        container.dayTextView.setTextColor(red)
                    else
                        container.dayTextView.setTextColor(redDusk)
                }

                container.dayTextView.isEnabled = active
                container.infoTextView.isEnabled = active

                if (count > 0)
                    container.infoTextView.text = "($count)"
                else
                    container.infoTextView.text = ""

                container.view.setOnClickListener {
                    if (count > 0) {
                        val intent = Intent(this@MainActivity, TaskViewActivity::class.java)
                        intent.putExtra("day", data.date.toEpochDay())
                        startActivity(intent)
                    } else {
                        val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
                        Toast.makeText(this@MainActivity, "No tasks scheduled on ${data.date.format(formatter)}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val daysOfWeek = daysOfWeek()

        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        val titlesContainer = findViewById<ViewGroup>(R.id.titlesContainer)
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val dayOfWeek = daysOfWeek[index]
                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title
            }

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy, MMM")

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                container.textView.text = data.yearMonth.format(formatter)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        calendarView.notifyCalendarChanged()
    }
}
