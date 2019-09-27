package com.haroldadmin.vector.benchmark.reflection

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.full.primaryConstructor

@RunWith(AndroidJUnit4::class)
@Ignore("Don't run benchmarks with regular builds")
internal class PrimaryConstructorAccess {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun accessThroughPrimaryConstructorProperty() = benchmarkRule.measureRepeated {
        val constructor = this::class.primaryConstructor
    }

    @Test
    fun accessThroughConstructorsListDotFirst() = benchmarkRule.measureRepeated {
        val constructor = this::class.constructors.first()
    }

    // Access through constructors list is faster
}
