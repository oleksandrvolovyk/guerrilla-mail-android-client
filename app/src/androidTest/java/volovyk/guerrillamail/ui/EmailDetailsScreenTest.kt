package volovyk.guerrillamail.ui

import android.content.Context
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.details.EmailDetailsScreen
import volovyk.guerrillamail.ui.details.EmailDetailsUiState
import volovyk.guerrillamail.ui.theme.GuerrillaMailTheme

@RunWith(AndroidJUnit4::class)
@LargeTest
class EmailDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun setSpecificEmailScreenState(
        state: EmailDetailsUiState,
        onHtmlRenderSwitchCheckedChange: (Boolean) -> Unit = {}
    ) {
        composeTestRule.setContent {
            GuerrillaMailTheme {
                EmailDetailsScreen(
                    uiState = state,
                    onHtmlRenderSwitchCheckedChange = onHtmlRenderSwitchCheckedChange
                )
            }
        }
    }

    @Test
    fun emailWithBodyAsText_IsDisplayed() {
        val email = Email(
            id = "id",
            from = "from",
            subject = "subject",
            textBody = "text body",
            filteredHtmlBody = "filtered html body",
            fullHtmlBody = "full html body",
            date = "date",
            viewed = true
        )

        setSpecificEmailScreenState(
            EmailDetailsUiState(
                email = email,
                renderHtml = false
            )
        )

        // Assert that email's "from" field is displayed
        composeTestRule
            .onNodeWithText(email.from)
            .assertExists()

        // Assert that email's subject is displayed
        composeTestRule
            .onNodeWithText(email.subject)
            .assertExists()

        // Assert that email's date is displayed
        composeTestRule
            .onNodeWithText(email.date)
            .assertExists()

        // Assert that email's body is displayed as text
        composeTestRule
            .onNodeWithText(email.textBody)
            .assertExists()

        // Assert that email body WebView is not displayed
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_email_body_web_view)))
            .assertDoesNotExist()
    }

    @Test
    fun emailWithBodyAsHtml_IsDisplayed() {
        val email = Email(
            id = "id",
            from = "from",
            subject = "subject",
            textBody = "text body",
            filteredHtmlBody = "filtered html body",
            fullHtmlBody = "full html body",
            date = "date",
            viewed = true
        )

        setSpecificEmailScreenState(
            EmailDetailsUiState(
                email = email,
                renderHtml = true
            )
        )

        // Assert that email's "from" field is displayed
        composeTestRule
            .onNodeWithText(email.from)
            .assertExists()

        // Assert that email's subject is displayed
        composeTestRule
            .onNodeWithText(email.subject)
            .assertExists()

        // Assert that email's date is displayed
        composeTestRule
            .onNodeWithText(email.date)
            .assertExists()

        // Assert that email body WebView is displayed
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_email_body_web_view)))
            .assertExists()

        // Assert that email's body is not displayed as text
        composeTestRule
            .onNodeWithText(email.textBody)
            .assertDoesNotExist()
    }

    @Test
    fun displayImagesSwitch_WhenHtmlRenderIsEnabled_IsShown() {
        setSpecificEmailScreenState(
            EmailDetailsUiState(
                email = null,
                renderHtml = true
            )
        )

        // Assert that 'Display Images' switch is shown
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_display_images_switch)))
            .assertExists()
    }

    @Test
    fun displayImagesSwitch_WhenHtmlRenderIsDisabled_IsNotShown() {
        setSpecificEmailScreenState(
            EmailDetailsUiState(
                email = null,
                renderHtml = false
            )
        )

        // Assert that 'Display Images' switch is not shown
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_display_images_switch)))
            .assertDoesNotExist()
    }

    @Test
    fun clickingHtmlRenderSwitch_switchesFromFalseToTrue() {
        var isHtmlRenderSwitchChecked = false

        setSpecificEmailScreenState(
            EmailDetailsUiState(
                email = null,
                renderHtml = isHtmlRenderSwitchChecked
            ),
            onHtmlRenderSwitchCheckedChange = { isHtmlRenderSwitchChecked = it }
        )

        // Perform click on the "HTML render switch"
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_html_render_switch)))
            .performClick()

        // Assert that the lambda is called and the state is updated
        assertTrue(isHtmlRenderSwitchChecked)
    }

    @Test
    fun clickingHtmlRenderSwitch_switchesFromTrueToFalse() {
        var isHtmlRenderSwitchChecked = true

        setSpecificEmailScreenState(
            EmailDetailsUiState(
                email = null,
                renderHtml = isHtmlRenderSwitchChecked
            ),
            onHtmlRenderSwitchCheckedChange = { isHtmlRenderSwitchChecked = it }
        )

        // Perform click on the "HTML render switch"
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_html_render_switch)))
            .performClick()

        // Assert that the lambda is called and the state is updated
        assertTrue(!isHtmlRenderSwitchChecked)
    }
}