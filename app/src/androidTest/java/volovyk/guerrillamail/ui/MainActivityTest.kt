package volovyk.guerrillamail.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.update
import org.hamcrest.core.IsNot.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.FakeEmailRepository
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.util.FakeMessageHandler
import volovyk.guerrillamail.util.MessageHandler
import volovyk.guerrillamail.util.State
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var emailRepository: EmailRepository
    private lateinit var fakeEmailRepository: FakeEmailRepository

    @Inject
    lateinit var messageHandler: MessageHandler
    private lateinit var fakeMessageHandler: FakeMessageHandler

    @Before
    fun init() {
        hiltRule.inject()
        fakeEmailRepository = emailRepository as FakeEmailRepository
        fakeMessageHandler = messageHandler as FakeMessageHandler
    }

    @Test
    fun initialUiStateTest() {
        // Check title in ActionBar
        onView(withText("Emails")).check(matches(isDisplayed()))
    }

    @Test
    fun uiCallsMessageHandlerToShowErrorMessages() {
        val preMessageHandlerCalls = fakeMessageHandler.callsCounter

        // Repository is in error state
        fakeEmailRepository.state.update { State.Failure(IOException()) }

        sleepUntil { fakeMessageHandler.callsCounter > preMessageHandlerCalls }

        assertEquals(preMessageHandlerCalls + 1, fakeMessageHandler.callsCounter)
    }

    @Test
    fun uiShowsSnackbarWhenMainRemoteEmailDatabaseIsNotAvailable() {
        // Main remote email database is not available
        fakeEmailRepository.mainRemoteEmailDatabaseAvailability.update { false }

        // Snackbar with correct text is shown
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(isDisplayed()))

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.guerrilla_mail_offline)))
    }

    @Test
    fun uiShowsLoadingIndicator() {
        // Repository is loading data
        fakeEmailRepository.state.update { State.Loading }

        // Check that refreshing spinner is shown
        onView(withId(R.id.refreshingSpinner)).check(matches(isDisplayed()))

        // Repository finished loading
        fakeEmailRepository.state.update { State.Success }

        // Check that refreshing spinner is not shown
        onView(withId(R.id.refreshingSpinner)).check(matches(not(isDisplayed())))
    }

    private inline fun sleepUntil(timeout: Int = 5000, predicate: () -> Boolean) {
        var timeCounter = 0
        while (!predicate()) {
            if (timeCounter >= timeout) throw TimeoutException()
            Thread.sleep(100)
            timeCounter += 100
        }
    }
}