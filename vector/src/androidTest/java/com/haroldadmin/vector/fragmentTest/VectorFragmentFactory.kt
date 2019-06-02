package com.haroldadmin.vector.fragmentTest

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

internal class VectorFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val clazz = loadFragmentClass(classLoader, className)

        return if (clazz == TestFragment::class.java) {
            TestFragment()
        } else {
            super.instantiate(classLoader, className)
        }
    }

}