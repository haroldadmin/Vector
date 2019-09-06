package com.haroldadmin.vector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.junit.Test

class ViewModelOwnerTest {

    @Test
    fun `Activity as a ViewModelOwner should contain the activity passed to it`() {
        val activity = TestActivity()
        val viewModelOwner = activity.activityViewModelOwner()
        viewModelOwner.activity<TestActivity>()
    }

    @Test
    fun `Fragment as a ViewModelOwner should contain the fragment passed to it`() {
        val fragment = TestFragment().apply { arguments = Bundle.EMPTY }
        val viewModelOwner = fragment.fragmentViewModelOwner()
        viewModelOwner.fragment<TestFragment>()
        assert(viewModelOwner.args() == Bundle.EMPTY)
    }
}

