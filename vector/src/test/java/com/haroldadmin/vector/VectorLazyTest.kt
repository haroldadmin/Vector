package com.haroldadmin.vector

import org.junit.Before
import org.junit.Test

class VectorLazyTest {

    lateinit var lazy: vectorLazy<Int>

    @Before
    fun setup() {
        lazy = vectorLazy { 42 }
    }

    @Test
    fun `should be instantiated lazily`() {
        assert(!lazy.isInitialized())
        lazy.value
        assert(lazy.isInitialized())
    }

    @Test
    fun `should hold correct value`() {
        val value = lazy.value
        assert(value == 42)
    }

}