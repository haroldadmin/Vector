package com.haroldadmin.vector.extensions

import com.haroldadmin.vector.compute
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.junit.After
import org.junit.Before
import org.junit.Test

class ChannelComputeExtensionTest {

    private val initialValue = 0
    lateinit var channel: ConflatedBroadcastChannel<Int>

    @Before
    fun setup() {
        channel = ConflatedBroadcastChannel(initialValue)
    }

    @Test
    fun `computing new value should replace old value`() {
        val isSuccessful = channel.compute { currentValue ->
            currentValue + 1
        }

        assert(isSuccessful)
        assert(channel.value == initialValue + 1)
    }

    @Test
    fun `computing new value should do nothing if the channel is closed`() {
        channel.close()
        val isSuccessful = channel.compute { currentValue -> currentValue + 1 }

        assert(!isSuccessful)
    }

    @After
    fun teardown() {
        channel.close()
    }
}