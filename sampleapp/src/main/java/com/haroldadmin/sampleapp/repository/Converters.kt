package com.haroldadmin.sampleapp.repository

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun colourToString(colour: Colour?): String? {
        return colour?.toString()
    }

    @TypeConverter
    fun stringToColour(str: String?): Colour? {
        return when (str) {
            Colour.BLUE.toString() -> Colour.BLUE
            Colour.RED.toString() -> Colour.RED
            Colour.GREEN.toString() -> Colour.GREEN
            Colour.PINK.toString() -> Colour.PINK
            Colour.YELLOW.toString() -> Colour.YELLOW
            else -> null
        }
    }
}