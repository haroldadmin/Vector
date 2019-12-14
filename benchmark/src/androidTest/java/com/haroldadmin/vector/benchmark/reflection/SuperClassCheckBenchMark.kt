package com.haroldadmin.vector.benchmark.reflection

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.full.isSuperclassOf

internal interface SuperTypeInterface
internal class ImplementingClass : SuperTypeInterface

@Ignore("Don't run benchmarks for regular builds")
@RunWith(AndroidJUnit4::class)
internal class SuperClassCheckBenchMark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun kotlinReflectionSuperClassCheck() {
        benchmarkRule.measureRepeated {
            val superClass = SuperTypeInterface::class
            val subClass = ImplementingClass::class
            superClass.isSuperclassOf(subClass)
        }
    }

    @Test
    fun javaReflectionSuperClassCheck() {
        benchmarkRule.measureRepeated {
            val superClass = SuperTypeInterface::class.java
            val subClass = ImplementingClass::class.java
            superClass.isAssignableFrom(subClass)
        }
    }

    // Result is that Kotlin Reflection is around 150x-200x slower than Java Reflection for this particular check
}