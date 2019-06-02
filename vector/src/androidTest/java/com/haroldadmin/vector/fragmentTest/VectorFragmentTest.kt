package com.haroldadmin.vector.fragmentTest

import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Job
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class VectorFragmentTest {

    private lateinit var fragmentFactory: VectorFragmentFactory

    @Before
    fun setup() {
        fragmentFactory = VectorFragmentFactory()
    }

    @Test
    fun fragmentScopeTest() {
        lateinit var job: Job

        val scenario = launchFragment<TestFragment>(factory = fragmentFactory)

        scenario.onFragment { fragment ->
            job = fragment.count()
        }

        assertTrue(job.isActive)

        scenario.moveToState(Lifecycle.State.DESTROYED)

        assertTrue(job.children.all { it.isCancelled })
        assertTrue(job.isCancelled)
    }

}