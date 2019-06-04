package com.haroldadmin.sampleapp.utils

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.haroldadmin.sampleapp.repository.EntitiesDb
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.sampleapp.repository.prepopulationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Provider(context: Context, coroutineScope: CoroutineScope) {

    val db = Room.databaseBuilder(context, EntitiesDb::class.java, "entities-db")
        .fallbackToDestructiveMigration()
        .addCallback(object: RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                coroutineScope.launch {
                  database.entityDao().addEntities(prepopulationData)
                }
            }
        } )
        .build()

    // To get access to the db inside the prepopulation callback
    private val database: EntitiesDb
        get() = db

    val entitiesRepository = EntitiesRepository(db.entityDao())

}