package volovyk.guerrillamail.ui

import android.content.Context
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import volovyk.guerrillamail.ui.specific.SpecificEmailScreen
import volovyk.guerrillamail.ui.specific.SpecificEmailUiState

@RunWith(AndroidJUnit4::class)
@LargeTest
class SpecificEmailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun setSpecificEmailScreenState(
        state: SpecificEmailUiState,
        onHtmlRenderSwitchCheckedChange: (Boolean) -> Unit = {}
    ) {
        composeTestRule.setContent {
            GuerrillaMailTheme {
                SpecificEmailScreen(state, onHtmlRenderSwitchCheckedChange)
            }
        }
    }

    @Test
    fun emailWithBodyAsText_IsDisplayed() {
        val email = Email(
            id = "id",
            from = "from",
            subject = "subject",
            body = "body",
            htmlBody = "html body",
            date = "date",
            viewed = true
        )

        setSpecificEmailScreenState(
            SpecificEmailUiState(
                email = email,
                renderHtml = false
            )
        )

        // Assert that email's "from" field is displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.from, email.from))
            .assertExists()

        // Assert that email's subject is displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.subject, email.subject))
            .assertExists()

        // Assert that email's date is displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.date, email.date))
            .assertExists()

        // Assert that email's body is displayed as text
        composeTestRule
            .onNodeWithText(email.body)
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
            body = "body",
            htmlBody = "html body",
            date = "date",
            viewed = true
        )

        setSpecificEmailScreenState(
            SpecificEmailUiState(
                email = email,
                renderHtml = true
            )
        )

        // Aseert that email's "from" field is displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.from, email.from))
            .assertExists()

        // Assert that email's subject is displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.subject, email.subject))
            .assertExists()

        // Assert that email's date is displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.date, email.date))
            .assertExists()

        // Assert that email body WebView is displayed
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_email_body_web_view)))
            .assertExists()

        // Assert that email's body is not displayed as text
        composeTestRule
            .onNodeWithText(email.body)
            .assertDoesNotExist()
    }

    @Test
    fun clickingHtmlRenderSwitch_switchesFromFalseToTrue() {
        var isHtmlRenderSwitchChecked = false

        setSpecificEmailScreenState(
            SpecificEmailUiState(
                email = null,
                renderHtml = isHtmlRenderSwitchChecked
            ),
            onHtmlRenderSwitchCheckedChange = { isHtmlRenderSwitchChecked = it }
        )

        // Perform click on the "HTML render switch"
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_html_render_swtich)))
            .performClick()

        // Assert that the lambda is called and the state is updated
        assertTrue(isHtmlRenderSwitchChecked)
    }

    @Test
    fun clickingHtmlRenderSwitch_switchesFromTrueToFalse() {
        var isHtmlRenderSwitchChecked = true

        setSpecificEmailScreenState(
            SpecificEmailUiState(
                email = null,
                renderHtml = isHtmlRenderSwitchChecked
            ),
            onHtmlRenderSwitchCheckedChange = { isHtmlRenderSwitchChecked = it }
        )

        // Perform click on the "HTML render switch"
        composeTestRule
            .onNode(hasTestTag(context.getString(R.string.test_tag_html_render_swtich)))
            .performClick()

        // Assert that the lambda is called and the state is updated
        assertTrue(!isHtmlRenderSwitchChecked)
    }
}