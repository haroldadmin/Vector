package com.haroldadmin.vector.benchmark.reflection

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// @Ignore("Don't run benchmarks with regular builds")
@RunWith(AndroidJUnit4::class)
class UnderlyingClassAccess {

    @get:Rule val benchmarkRule = BenchmarkRule()

    @Test
    fun kotlinClassAccess() {
        benchmarkRule.measureRepeated {
            val klass = ImplementingClass::class
        }
    }

    @Test
    fun javaClassAccess() {
        benchmarkRule.measureRepeated {
            val clazz = ImplementingClass::class.java
        }
    }

    @Test
    fun javaClassToKotlin() {
        benchmarkRule.measureRepeated {
            val klass = ImplementingClass::class.java.kotlin
        }
    }

    // Accessing the underlying java class or kotlin class is equivalent in performance
}