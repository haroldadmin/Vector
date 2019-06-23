package com.haroldadmin.sampleapp.repository

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
