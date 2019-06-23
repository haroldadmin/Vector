package com.haroldadmin.sampleapp.repository

import kotlin.random.Random
import kotlin.random.nextInt

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

fun getRandomColour() = when (Random(System.currentTimeMillis()).nextInt(1..5)) {
    1 -> Colour.RED
    2 -> Colour.BLUE
    3 -> Colour.GREEN
    4 -> Colour.YELLOW
    else -> Colour.PINK
}