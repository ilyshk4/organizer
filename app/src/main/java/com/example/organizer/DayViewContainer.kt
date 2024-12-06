package com.example.organizer

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer

@RequiresApi(Build.VERSION_CODES.O)
class DayViewContainer(view: View) : ViewContainer(view) {
    val dayTextView: TextView = view.findViewById(R.id.calendarDayText)
    val infoTextView: TextView = view.findViewById(R.id.infoText)
    val panel: View = view.findViewById(R.id.panel)

    lateinit var day: CalendarDay
    var taskCount: Int = 0
}