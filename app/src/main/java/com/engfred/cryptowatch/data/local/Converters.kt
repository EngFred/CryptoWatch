package com.engfred.cryptowatch.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSparklineList(value: List<Double>?): String {
        return gson.toJson(value ?: emptyList<Double>())
    }

    @TypeConverter
    fun toSparklineList(value: String): List<Double> {
        val type = object : TypeToken<List<Double>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}