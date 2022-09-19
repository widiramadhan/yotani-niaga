package com.javaindoku.yotaniniaga.service.database

import androidx.room.TypeConverter
import com.javaindoku.yotaniniaga.utilities.extension.fromJson
import com.javaindoku.yotaniniaga.utilities.extension.toJson

class Converters {
    @TypeConverter
    fun fromList(value: String?): ArrayList<String>? = value?.let { value.fromJson() }

    @TypeConverter
    fun fromString(value: ArrayList<String>?): String? = value?.let { value.toJson() }
}