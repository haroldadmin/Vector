package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

internal class TestFragmentFactory(private val vmFactory: TestViewModelFactory) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val clazz = loadFragmentClass(classLoader, className)

        return if (clazz == TestFragment::class.java) {
            TestFragment(vmFactory)
        } else {
            super.instantiate(classLoader, className)
        }
    }
}