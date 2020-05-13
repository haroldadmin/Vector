package com.haroldadmin.hellovector

import android.os.Build
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class HelloFragmentTest {

    @Test
    @Ignore("Test passes on local device, but fails on CI for some reason")
    fun shouldFetchMessageWhenLaunched() {
        val scenario = launchFragmentInContainer<HelloFragment>()
        scenario.onFragment { fragment ->
            onView(withId(R.id.messageTextView)).check { view, _ ->
                view as TextView
                assert(view.text == HelloState.loadingMessage) {
                    "Expected loading text, found ${view.text}"
                }
            }
        }
    }
}