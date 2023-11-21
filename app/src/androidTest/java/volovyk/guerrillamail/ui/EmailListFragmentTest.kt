package volovyk.guerrillamail.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.FakeEmailRepository
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.list.EmailListFragment
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class EmailListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var emailRepository: EmailRepository
    private lateinit var fakeEmailRepository: FakeEmailRepository

    @Before
    fun init() {
        hiltRule.inject()
        fakeEmailRepository = emailRepository as FakeEmailRepository
        launchFragmentInHiltContainer<EmailListFragment>()
    }

    private fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }

    @Test
    fun recyclerViewShowsEmails() {
        // Generate a list of 3 sample Email objects
        val emails = List(3) { index ->
            Email(
                id = "$index",
                from = "from$index@example.com",
                subject = "Subject $index",
                body = "Body $index",
                htmlBody = "HtmlBody $index",
                date = "Date $index",
                viewed = false
            )
        }

        // Update the fake email repository with the generated list of emails
        fakeEmailRepository.emails.update { emails }

        // Iterate through each email in the list and perform UI tests on the RecyclerView
        emails.forEachIndexed { index, email ->
            // Check if the RecyclerView item at the given position contains
            // the expected 'from' and 'subject' values
            onView(withRecyclerView(R.id.list).atPosition(index))
                .check(matches(hasDescendant(withText(email.from))))
                .check(matches(hasDescendant(withText(email.subject))))
        }
    }
}