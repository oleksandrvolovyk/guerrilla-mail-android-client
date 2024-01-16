package volovyk.guerrillamail.ui

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.compose.GuerrillaMailTheme
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.list.EmailListScreen

@RunWith(AndroidJUnit4::class)
@LargeTest
class EmailListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun setEmailListState(
        emails: List<Email>,
        onItemClick: (Email) -> Unit = {},
        onItemDeleteButtonClick: (Email) -> Unit = {},
        onItemDeleteButtonLongClick: (Email) -> Unit = {}
    ) {
        composeTestRule.setContent {
            GuerrillaMailTheme {
                EmailListScreen(emails, onItemClick, onItemDeleteButtonClick, onItemDeleteButtonLongClick)
            }
        }
    }

    @Test
    fun emailList_ShowsEmails() {
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

        setEmailListState(emails = emails)

        emails.forEach { email ->
            composeTestRule
                .onNodeWithText(email.from)
                .assertExists()

            composeTestRule
                .onNodeWithText(email.subject)
                .assertExists()
        }
    }

    @Test
    fun clickingEmailListItem_InvokesLambda() {
        val emails = listOf(
            Email(
                id = "0",
                from = "from@example.com",
                subject = "Subject",
                body = "Body",
                htmlBody = "HtmlBody",
                date = "Date",
                viewed = false
            )
        )

        var lambdaWasInvoked = false
        setEmailListState(emails = emails, onItemClick = { email ->
            if (email == emails[0]) {
                lambdaWasInvoked = true
            } else {
                throw IllegalStateException(
                    "Wrong Email object passed to lambda!\n" +
                            "Expected: ${emails[0]}\n" +
                            "Received: $email"
                )
            }
        })

        // Perform click on the EmailListItem
        composeTestRule
            .onNodeWithText(emails[0].subject)
            .performClick()

        // Assert lambda was invoked
        assertTrue(lambdaWasInvoked)
    }

    @Test
    fun clickingEmailListItemDeleteButton_InvokesLambda() {
        val emails = listOf(
            Email(
                id = "0",
                from = "from@example.com",
                subject = "Subject",
                body = "Body",
                htmlBody = "HtmlBody",
                date = "Date",
                viewed = false
            )
        )

        var lambdaWasInvoked = false
        setEmailListState(emails = emails, onItemDeleteButtonClick = { email ->
            if (email == emails[0]) {
                lambdaWasInvoked = true
            } else {
                throw IllegalStateException(
                    "Wrong Email object passed to lambda!\n" +
                            "Expected: ${emails[0]}\n" +
                            "Received: $email"
                )
            }
        })

        // Perform click on the EmailListItem "Delete" button
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.delete))
            .performClick()

        // Assert lambda was invoked
        assertTrue(lambdaWasInvoked)
    }

    @Test
    fun longClickingEmailListItemDeleteButton_InvokesLambda() {
        val emails = listOf(
            Email(
                id = "0",
                from = "from@example.com",
                subject = "Subject",
                body = "Body",
                htmlBody = "HtmlBody",
                date = "Date",
                viewed = false
            )
        )

        var lambdaWasInvoked = false
        setEmailListState(emails = emails, onItemDeleteButtonLongClick = { email ->
            if (email == emails[0]) {
                lambdaWasInvoked = true
            } else {
                throw IllegalStateException(
                    "Wrong Email object passed to lambda!\n" +
                            "Expected: ${emails[0]}\n" +
                            "Received: $email"
                )
            }
        })

        // Perform long click on the EmailListItem "Delete" button
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.delete))
            .performTouchInput { longClick() }

        // Assert lambda was invoked
        assertTrue(lambdaWasInvoked)
    }
}