package volovyk.guerrillamail.ui

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import volovyk.guerrillamail.ui.theme.GuerrillaMailTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import volovyk.guerrillamail.R
import volovyk.guerrillamail.ui.assigned.AssignedEmailCard

@RunWith(AndroidJUnit4::class)
@LargeTest
class AssignedEmailCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun setAssignedEmailCardState(
        emailUsername: String?,
        emailDomain: String?,
        isGetNewAddressButtonVisible: Boolean
    ) {
        composeTestRule.setContent {
            GuerrillaMailTheme {
                AssignedEmailCard(
                    emailUsername = emailUsername,
                    emailDomain = emailDomain,
                    isGetNewAddressButtonVisible = isGetNewAddressButtonVisible
                )
            }
        }
    }

    @Test
    fun gettingTemporaryEmailText_IsShown() {
        setAssignedEmailCardState(
            emailUsername = null,
            emailDomain = null,
            isGetNewAddressButtonVisible = false
        )

        // Assert that "Getting temporary email..." text is displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.getting_temporary_email))
            .assertExists()
    }

    @Test
    fun assignedEmailAddress_IsShown() {
        val emailUsername = "test"
        val emailDomain = "example.com"

        setAssignedEmailCardState(
            emailUsername = emailUsername,
            emailDomain = emailDomain,
            isGetNewAddressButtonVisible = false
        )

        // Assert "Getting temporary email" is not displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.getting_temporary_email))
            .assertDoesNotExist()

        // Assert assigned email username is displayed
        composeTestRule
            .onNodeWithText(emailUsername)
            .assertExists()

        // Assert assigned email domain is displayed
        composeTestRule
            .onNodeWithText("@$emailDomain")
            .assertExists()
    }

    @Test
    fun getNewAddressButton_IsNotShown() {
        setAssignedEmailCardState(
            emailUsername = null,
            emailDomain = null,
            isGetNewAddressButtonVisible = false
        )

        // Assert that "Get new address" button is not displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.get_new_address))
            .assertDoesNotExist()
    }

    @Test
    fun getNewAddressButton_IsShown() {
        setAssignedEmailCardState(
            emailUsername = null,
            emailDomain = null,
            isGetNewAddressButtonVisible = true
        )

        // Assert that "Get new address" button is displayed
        composeTestRule
            .onNodeWithText(context.getString(R.string.get_new_address))
            .assertExists()
    }
}