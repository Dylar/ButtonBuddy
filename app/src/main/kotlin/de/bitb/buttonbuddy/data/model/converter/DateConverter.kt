package de.bitb.buttonbuddy.data.model.converter

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromDate(date: Date?): Long = date?.time ?: 0

    @TypeConverter
    fun toDate(value: Long): Date = Date(value)
}