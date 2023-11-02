package com.techiq.wallpaperwonders.database

import androidx.room.TypeConverter

class Conveter {

    @TypeConverter
    fun fromAny(value: Any?): String {
        return value.toString()
    }


}