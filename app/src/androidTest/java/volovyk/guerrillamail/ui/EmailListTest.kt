package volovyk.guerrillamail.ui

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.list.EmailListScreen
import volovyk.guerrillamail.ui.list.EmailListUiState
import volovyk.guerrillamail.ui.list.SelectableItem
import volovyk.guerrillamail.ui.theme.GuerrillaMailTheme

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
        uiState: EmailListUiState,
        onItemClick: (Email) -> Unit = {},
        onItemLongClick: (Email) -> Unit = {},
        onClearSelectionButtonClick: () -> Unit = {},
        onSelectAllButtonClick: () -> Unit = {},
        onDeleteButtonClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            GuerrillaMailTheme {
                EmailListScreen(
                    uiState = uiState,
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick,
                    onClearSelectionButtonClick = onClearSelectionButtonClick,
                    onSelectAllButtonClick = onSelectAllButtonClick,
                    onDeleteButtonClick = onDeleteButtonClick
                )
            }
        }
    }

    @Test
    fun emailList_ShowsEmails() {
        // Generate a list of 3 sample Email objects
        val uiState = EmailListUiState(
            List(2) { index ->
                SelectableItem(
                    item = Email(
                        id = "$index",
                        from = "from$index@example.com",
                        subject = "Subject $index",
                        textBody = "Text body $index",
                        filteredHtmlBody = "Filtered Html Body $index",
                        fullHtmlBody = "Full Html Body $index",
                        date = "Date $index",
                        viewed = false
                    )
                )
            }
        )

        setEmailListState(uiState = uiState)

        uiState.emails.forEach { email ->
            composeTestRule
                .onNodeWithText(email.item.from)
                .assertExists()

            composeTestRule
                .onNodeWithText(email.item.subject)
                .assertExists()
        }
    }

    @Test
    fun clickingEmailListItem_InvokesLambda() {
        val uiState = EmailListUiState(
            listOf(
                SelectableItem(
                    item = Email(
                        id = "0",
                        from = "from@example.com",
                        subject = "Subject",
                        textBody = "Text body",
                        filteredHtmlBody = "Filtered Html Body",
                        fullHtmlBody = "Full Html Body",
                        date = "Date",
                        viewed = false
                    )
                )
            )
        )

        var lambdaWasInvoked = false
        setEmailListState(uiState = uiState, onItemClick = { email ->
            if (email == uiState.emails[0].item) {
                lambdaWasInvoked = true
            } else {
                throw IllegalStateException(
                    "Wrong Email object passed to lambda!\n" +
                            "Expected: ${uiState.emails[0].item}\n" +
                            "Received: $email"
                )
            }
        })

        // Perform click on the EmailListItem
        composeTestRule
            .onNodeWithText(uiState.emails[0].item.subject)
            .performClick()

        // Assert lambda was invoked
        assertTrue(lambdaWasInvoked)
    }

    @Test
    fun longClickingEmailListItem_InvokesLambda() {
        val uiState = EmailListUiState(
            listOf(
                SelectableItem(
                    item = Email(
                        id = "0",
                        from = "from@example.com",
                        subject = "Subject",
                        textBody = "Text body",
                        filteredHtmlBody = "Filtered Html Body",
                        fullHtmlBody = "Full Html Body",
                        date = "Date",
                        viewed = false
                    )
                )
            )
        )

        var lambdaWasInvoked = false
        setEmailListState(uiState = uiState, onItemLongClick = { email ->
            if (email == uiState.emails[0].item) {
                lambdaWasInvoked = true
            } else {
                throw IllegalStateException(
                    "Wrong Email object passed to lambda!\n" +
                            "Expected: ${uiState.emails[0].item}\n" +
                            "Received: $email"
                )
            }
        })

        // Perform long click on the EmailListItem "Delete" button
        composeTestRule
            .onNodeWithText(uiState.emails[0].item.subject)
            .performTouchInput { longClick() }

        // Assert lambda was invoked
        assertTrue(lambdaWasInvoked)
    }
}