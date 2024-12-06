package com.example.organizer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

const val DATABASE_NAME = "Tasks.db"
const val DATABASE_VERSION = 1
const val TABLE_NAME = "tasks"
const val COLUMN_ID = "id"
const val COLUMN_TITLE = "title"
const val COLUMN_DATE = "date"
const val COLUMN_DESC = "desc"

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_ID INTEGER PRIMARY KEY," +
            "$COLUMN_TITLE TEXT," +
            "$COLUMN_DATE TEXT," +
            "$COLUMN_DESC INTEGER)"

class TaskDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        lateinit var instance: TaskDbHelper
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertTask(task: Task) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DATE, task.date.toEpochDay())
            put(COLUMN_DESC, task.description)
        }

        db.insert(TABLE_NAME, null, values)
    }

    private fun getCursorForDate(day: Long): Cursor {
        val db = readableDatabase

        val selection = "$COLUMN_DATE = ?"
        val selectionArgs = arrayOf(day.toString())

        return db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }

    fun getTaskCount(day: Long): Int {
        val cursor = getCursorForDate(day)
        val count = cursor.count
        cursor.close()
        return count
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTasks(day: Long): List<Task> {
        val cursor = getCursorForDate(day)
        val tasks = mutableListOf<Task>()
        while (cursor.moveToNext()) {
            tasks.add(Task(
                cursor.getString(1),
                LocalDate.ofEpochDay(cursor.getLong(2)),
                cursor.getString(3),
                cursor.getInt(0),
            ));
        }
        cursor.close()
        return tasks
    }

    fun finishTask(id: Int) {
        val db = writableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        db.delete(TABLE_NAME, selection, selectionArgs)
    }
}