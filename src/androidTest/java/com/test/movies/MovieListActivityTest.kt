package com.test.movies

import android.os.SystemClock
import android.widget.AutoCompleteTextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.test.movies.uicomponents.MovieListActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MovieListActivityTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.test.movies", appContext.packageName)
    }

    @Rule
    @JvmField
    var activityTestRule: ActivityTestRule<MovieListActivity> =
        ActivityTestRule(MovieListActivity::class.java)

    /*@Test
    public void checkLogin_backgroundImage() {
        onView(withDrawable(R.drawable.login_background)).check(matches(isDisplayed()));
    }*/

    @Test
    fun isViewAvailable() {
        SystemClock.sleep(3500)
        Espresso.onView(withId(R.id.moviesRecyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.titleSearch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btnLogout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testRecyclerView() {
        Espresso.onView(withId(R.id.moviesRecyclerView)).perform(ViewActions.swipeUp())
    }

    @Test
    fun testSearchView() {
        SystemClock.sleep(3500)
        onView(isAssignableFrom(AutoCompleteTextView::class.java))
            .perform(typeText("da"))
    }

    @Test
    fun testLogout() {
        Espresso.onView(withId(R.id.btnLogout))
            .perform(ViewActions.click())
    }
}