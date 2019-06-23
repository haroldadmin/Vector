package com.haroldadmin.sampleapp.utils

import android.content.Context
import com.haroldadmin.sampleapp.CountingEntity
import com.haroldadmin.sampleapp.Database
import com.haroldadmin.sampleapp.repository.Colour
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

class Provider(context: Context) {

    private val sqlDelightDriver: SqlDriver = AndroidSqliteDriver(Database.Schema, context, "countingEntities.db")

    private val colourAdapter = object : ColumnAdapter<Colour, String> {
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

    val database: Database by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Database(
            driver = sqlDelightDriver,
            countingEntityAdapter = CountingEntity.Adapter(colourAdapter)
        )
    }
}