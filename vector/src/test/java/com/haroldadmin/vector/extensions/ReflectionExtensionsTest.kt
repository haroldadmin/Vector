package com.haroldadmin.vector.extensions

import com.haroldadmin.vector.DoesNotImplementVectorVMFactoryException
import com.haroldadmin.vector.TestViewModel
import com.haroldadmin.vector.TestViewModelWithFactory
import com.haroldadmin.vector.companionObject
import com.haroldadmin.vector.factoryCompanion
import com.haroldadmin.vector.instance
import com.haroldadmin.vector.state.CountingState
import org.junit.Test

class ReflectionExtensionsTest {

    @Test
    fun `Class newInstance test`() {
        val instance = CountingState::class.java.instance(42)
        instance as CountingState
        assert(instance.count == CountingState(42).count)
    }

    @Test
    fun `Class Companion object test`() {
        val companion = ClassWithCompanion::class.java.companionObject()
        assert(companion != null)
    }

    @Test
    fun `ViewModelFactory Companion test when factory exists`() {
        // Test should fail because this operation throws an error when there's no companion
        TestViewModelWithFactory::class.java.factoryCompanion()
    }

    @Test(expected = DoesNotImplementVectorVMFactoryException::class)
    fun `ViewModelFactory Companion test when factory does not exist`() {
        TestViewModel::class.java.factoryCompanion()
    }
}

private class ClassWithCompanion {
    companion object
}