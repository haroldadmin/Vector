package com.haroldadmin.sampleapp

import android.content.Context
import com.haroldadmin.sampleapp.utils.ColourAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides

@Module
object RepositoryModule {
    @JvmStatic
    @Provides
    fun database(context: Context): Database {
        val driver = AndroidSqliteDriver(Database.Schema, context, "countingEntities.db")
        val adapter = CountingEntity.Adapter(ColourAdapter)
        return Database.invoke(driver, adapter)
    }

    @JvmStatic
    @Provides
    fun countingQueries(database: Database): CountingEntityQueries {
        return database.countingEntityQueries
    }
}
