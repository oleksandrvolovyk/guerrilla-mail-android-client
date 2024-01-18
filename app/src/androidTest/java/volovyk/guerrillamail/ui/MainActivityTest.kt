package volovyk.guerrillamail.ui

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
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
import volovyk.guerrillamail.util.State
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var context: Context

    @Inject
    lateinit var emailRepository: EmailRepository
    private lateinit var fakeEmailRepository: FakeEmailRepository

    @Before
    fun init() {
        hiltRule.inject()
        fakeEmailRepository = emailRepository as FakeEmailRepository
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun uiShowsAndDismissesSnackbarDependingOnMainRemoteEmailDatabaseAvailability() {
        // Main remote email database is not available
        fakeEmailRepository.mainRemoteEmailDatabaseAvailability.update { false }

        // Snackbar with correct text and action is shown
        composeTestRule
            .onNodeWithText(context.getString(R.string.guerrilla_mail_offline))
            .assertExists()

        composeTestRule
            .onNodeWithText(context.getString(R.string.retry))
            .assertExists()

        // Main remote email database becomes available
        fakeEmailRepository.mainRemoteEmailDatabaseAvailability.update { true }

        // Snackbar is dismissed
        composeTestRule
            .onNodeWithText(context.getString(R.string.guerrilla_mail_offline))
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(context.getString(R.string.retry))
            .assertDoesNotExist()
    }

    @Test
    fun uiShowsLoadingIndicator() {
        // Repository is loading data
        fakeEmailRepository.state.update { State.Loading }

        // Loading indicator is shown
        composeTestRule
            .onNodeWithTag(context.getString(R.string.test_tag_loading_indicator))
            .assertExists()

        // Repository finished loading
        fakeEmailRepository.state.update { State.Success }

        // Loading indicator is not shown
        composeTestRule
            .onNodeWithTag(context.getString(R.string.test_tag_loading_indicator))
            .assertDoesNotExist()
    }
}