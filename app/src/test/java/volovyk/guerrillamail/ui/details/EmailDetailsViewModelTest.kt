package volovyk.guerrillamail.ui.details

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import volovyk.MainCoroutineRule
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.preferences.PreferencesRepository

@OptIn(ExperimentalCoroutinesApi::class)
class EmailDetailsViewModelTest {

    private lateinit var viewModel: EmailDetailsViewModel
    private lateinit var emailRepository: EmailRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var savedStateHandle: SavedStateHandle

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        emailRepository = mockk<EmailRepository>()
        preferencesRepository = mockk<PreferencesRepository>()
        savedStateHandle = SavedStateHandle()
    }

    @Test
    fun `init with emailId sets uiState email when emailId is provided`() = runTest {
        // Given
        val emailId = "123"
        val email = Email("123", "", "", "", "", "", false)

        savedStateHandle["emailId"] = emailId
        coEvery { emailRepository.getEmailById(emailId) } returns email
        coEvery { preferencesRepository.getValue(EmailDetailsViewModel.HTML_RENDER_KEY) } returns "true"

        // When
        viewModel = EmailDetailsViewModel(emailRepository, preferencesRepository)

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(email, uiState.email)
    }

    @Test
    fun `init without emailId does not set uiState email`() = runTest {
        // Given
        savedStateHandle.remove<Int>("emailId")

        // When
        viewModel = EmailDetailsViewModel(emailRepository, preferencesRepository)

        // Then
        val uiState = viewModel.uiState.value
        assertNull(uiState.email)
    }
}