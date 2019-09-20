package com.haroldadmin.vector.benchmark.reflection

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.full.createInstance

@RunWith(AndroidJUnit4::class)
@Ignore("Don't run benchmarks with regular builds")
internal class NewInstanceCreation {

    @get:Rule val benchmarkRule = BenchmarkRule()

    @Test
    fun kotlinReflectionNewInstanceCreation() = benchmarkRule.measureRepeated {
        val instance = ImplementingClass::class.createInstance()
    }

    @Test
    fun javaReflectionNewInstanceCreation() = benchmarkRule.measureRepeated {
        val instance = ImplementingClass::class.java.newInstance()
    }

    // Java Reflection version is 6x-7x faster than the kotlin version
}