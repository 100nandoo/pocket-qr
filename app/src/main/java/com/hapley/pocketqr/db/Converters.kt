package com.hapley.pocketqr.db

import androidx.room.TypeConverter
import java.util.Calendar

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

class Converters {
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}
