package com.example.organizer

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kizitonwose.calendar.view.ViewContainer

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.monthTitleText)
}