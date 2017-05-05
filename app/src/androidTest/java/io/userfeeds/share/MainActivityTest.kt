package io.userfeeds.share

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @Rule @JvmField
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun start() {
        onView(withText("Main!")).check(matches(isDisplayed()))
    }
}
