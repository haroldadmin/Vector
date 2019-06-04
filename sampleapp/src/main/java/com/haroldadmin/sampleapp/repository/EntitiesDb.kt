package com.haroldadmin.sampleapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CountingEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EntitiesDb : RoomDatabase() {
    abstract fun entityDao(): EntitiesDao
}