package com.haroldadmin.sampleapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entities")
data class CountingEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "counter")
    val counter: Int,
    @ColumnInfo(name = "colour")
    val colour: Colour
)

enum class Colour {
    RED {
        override fun toString(): String = "#ff1744"
    },
    BLUE {
        override fun toString(): String = "#42a5f5"
    },
    GREEN {
        override fun toString(): String = "#66bb6a"
    },
    YELLOW {
        override fun toString(): String = "#fbc02d"
    },
    PINK {
        override fun toString(): String = "#ec407a"
    }
}

val prepopulationData = listOf<CountingEntity>(
    CountingEntity("Books", 10, Colour.BLUE),
    CountingEntity("Pencils", 5, Colour.YELLOW),
    CountingEntity("Balls", 1, Colour.GREEN),
    CountingEntity("Rackets", 2, Colour.PINK)
)