package com.example.organizer

import java.time.LocalDate

data class Task(val title: String, val date: LocalDate, val description: String, val id: Int = -1)
