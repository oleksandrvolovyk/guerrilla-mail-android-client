package volovyk.guerrillamail.ui

import android.text.Html
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.FakeEmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.specific.SpecificEmailFragment
import volovyk.guerrillamail.ui.specific.SpecificEmailViewModel

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class SpecificEmailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val email = Email(
        id = "id",
        from = "from",
        subject = "subject",
        body = "body",
        date = "date",
        viewed = true
    )

    private val fakeEmailRepository = FakeEmailRepository(initialEmails = listOf(email))

    @BindValue
    val fakeSpecificEmailViewModel =
        SpecificEmailViewModel(SavedStateHandle(mapOf("emailId" to email.id)), fakeEmailRepository)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testingTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        launchFragmentInHiltContainer<SpecificEmailFragment> {}

        onView(withId(R.id.fromTextView))
            .check(matches(withText(appContext.getString(R.string.from, email.from))))
        onView(withId(R.id.subjectTextView))
            .check(matches(withText(appContext.getString(R.string.subject, email.subject))))
        onView(withId(R.id.dateTextView))
            .check(matches(withText(appContext.getString(R.string.date, email.date))))
        onView(withId(R.id.bodyTextView)).check(
            matches(
                withText(Html.fromHtml(email.body, Html.FROM_HTML_MODE_COMPACT).toString())
            )
        )
    }
}