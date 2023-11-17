package volovyk.guerrillamail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.FakeEmailRepository
import volovyk.guerrillamail.data.FakePreferencesRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.specific.SpecificEmailFragment
import volovyk.guerrillamail.ui.specific.SpecificEmailViewModel

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class SpecificEmailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    lateinit var fakeSpecificEmailViewModel: SpecificEmailViewModel

    @Test
    fun fragmentShowsEmail() {
        val email = Email(
            id = "id",
            from = "from",
            subject = "subject",
            body = "body",
            htmlBody = "html body",
            date = "date",
            viewed = true
        )

        // Set up fake repositories
        val fakeEmailRepository = FakeEmailRepository(initialEmails = listOf(email))
        val fakePreferencesRepository =
            FakePreferencesRepository(mapOf(SpecificEmailViewModel.HTML_RENDER_KEY to "false"))

        // Create a SpecificEmailViewModel with the fake repositories
        fakeSpecificEmailViewModel = SpecificEmailViewModel(
            SavedStateHandle(mapOf("emailId" to email.id)),
            fakeEmailRepository,
            fakePreferencesRepository
        )

        // Inject fake ViewModel
        hiltRule.inject()

        // Launch the SpecificEmailFragment
        launchFragmentInHiltContainer<SpecificEmailFragment>()

        // Get the application context for resource string retrieval
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Check that email's "from" field is shown
        onView(withId(R.id.fromTextView))
            .check(matches(withText(appContext.getString(R.string.from, email.from))))
        // Check that email's subject is shown
        onView(withId(R.id.subjectTextView))
            .check(matches(withText(appContext.getString(R.string.subject, email.subject))))
        // Check that email's date is shown
        onView(withId(R.id.dateTextView))
            .check(matches(withText(appContext.getString(R.string.date, email.date))))
        // Check that email's body is shown as text
        onView(withId(R.id.bodyTextView))
            .check(matches(withText(email.body)))
        // Check that WebView is not shown
        onView(withId(R.id.emailBodyWebView))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun fragmentShowsEmailsBodyInWebView() {
        val email = Email(
            id = "id",
            from = "from",
            subject = "subject",
            body = "body",
            htmlBody = "html body",
            date = "date",
            viewed = true
        )

        // Set up fake repositories
        val fakeEmailRepository = FakeEmailRepository(initialEmails = listOf(email))
        val fakePreferencesRepository =
            FakePreferencesRepository(mapOf(SpecificEmailViewModel.HTML_RENDER_KEY to "true"))

        // Create a SpecificEmailViewModel with the fake repositories
        fakeSpecificEmailViewModel = SpecificEmailViewModel(
            SavedStateHandle(mapOf("emailId" to email.id)),
            fakeEmailRepository,
            fakePreferencesRepository
        )

        // Inject fake ViewModel
        hiltRule.inject()

        // Launch the SpecificEmailFragment
        launchFragmentInHiltContainer<SpecificEmailFragment>()

        // Check that email body TextView is not shown
        onView(withId(R.id.bodyTextView))
            .check(matches(not(isDisplayed())))
        // Check that WebView is shown
        onView(withId(R.id.emailBodyWebView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickingHtmlRenderSwitchChangesEmailBodyDisplayMethod() {
        val email = Email(
            id = "id",
            from = "from",
            subject = "subject",
            body = "body",
            htmlBody = "html body",
            date = "date",
            viewed = true
        )

        // Set up fake repositories
        val fakeEmailRepository = FakeEmailRepository(initialEmails = listOf(email))
        val fakePreferencesRepository =
            FakePreferencesRepository(mapOf(SpecificEmailViewModel.HTML_RENDER_KEY to "false"))

        // Create a SpecificEmailViewModel with the fake repositories
        fakeSpecificEmailViewModel = SpecificEmailViewModel(
            SavedStateHandle(mapOf("emailId" to email.id)),
            fakeEmailRepository,
            fakePreferencesRepository
        )

        // Inject fake ViewModel
        hiltRule.inject()

        // Launch the SpecificEmailFragment
        launchFragmentInHiltContainer<SpecificEmailFragment>()

        // Check that email body TextView is shown
        onView(withId(R.id.bodyTextView))
            .check(matches(isDisplayed()))
        // Check that WebView is not shown
        onView(withId(R.id.emailBodyWebView))
            .check(matches(not(isDisplayed())))

        // Click on the html render switch
        onView(withId(R.id.htmlRenderSwitch))
            .perform(click())

        // Check that email body TextView is not shown
        onView(withId(R.id.bodyTextView))
            .check(matches(not(isDisplayed())))
        // Check that WebView is shown
        onView(withId(R.id.emailBodyWebView))
            .check(matches(isDisplayed()))

        // Click on the html render switch
        onView(withId(R.id.htmlRenderSwitch))
            .perform(click())

        // Check that email body TextView is shown
        onView(withId(R.id.bodyTextView))
            .check(matches(isDisplayed()))
        // Check that WebView is not shown
        onView(withId(R.id.emailBodyWebView))
            .check(matches(not(isDisplayed())))
    }
}