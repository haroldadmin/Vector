package com.haroldadmin.sampleapp.utils

import com.haroldadmin.sampleapp.repository.Colour
import com.squareup.sqldelight.ColumnAdapter

object ColourAdapter : ColumnAdapter<Colour, String> {
    override fun decode(databaseValue: String): Colour {
        return when (databaseValue) {
            Colour.BLUE.toString() -> Colour.BLUE
            Colour.RED.toString() -> Colour.RED
            Colour.GREEN.toString() -> Colour.GREEN
            Colour.PINK.toString() -> Colour.PINK
            Colour.YELLOW.toString() -> Colour.YELLOW
            else -> throw IllegalArgumentException("Unknown colour value requested")
        }
    }

    override fun encode(value: Colour): String = value.toString()
}
