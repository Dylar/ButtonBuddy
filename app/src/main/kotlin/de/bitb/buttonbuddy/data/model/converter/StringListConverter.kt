package de.bitb.buttonbuddy.data.model.converter

import android.text.TextUtils
import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromStringList(list: MutableList<String?>?): String =
        if (list == null) "" else TextUtils.join(",", list)

    @TypeConverter
    fun toStringList(value: String): MutableList<String> =
        mutableListOf(*value.split(",").toTypedArray())
}