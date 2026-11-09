package com.iva.mytasks

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class AddTaskFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addTask_appearsInList() {
        // Уникално име, за да не се бърка със задачи от предишни пускания
        val title = "Espresso task ${System.currentTimeMillis()}"

        onView(withId(R.id.fabAdd)).perform(click())
        onView(withId(R.id.editTitle)).perform(replaceText(title))
        closeSoftKeyboard()
        onView(withId(R.id.buttonSave)).perform(click())

        onView(withText(title)).check(matches(isDisplayed()))
    }

    @Test
    fun saveWithEmptyTitle_showsError() {
        onView(withId(R.id.fabAdd)).perform(click())
        onView(withId(R.id.buttonSave)).perform(click())

        onView(withText(R.string.title_required)).check(matches(isDisplayed()))
    }
}
