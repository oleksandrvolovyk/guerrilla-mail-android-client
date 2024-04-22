package volovyk.guerrillamail.ui.details

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

        viewModel = EmailDetailsViewModel(emailRepository, preferencesRepository)
    }

    @Test
    fun `loadEmail(emailId) sets uiState email when emailId is valid`() = runTest {
        // Given
        val emailId = "123"
        val email = Email("123", "", "", "", "", "", "", false)

        coEvery { emailRepository.getEmailById(emailId) } returns email
        coEvery { preferencesRepository.getValue(EmailDetailsViewModel.HTML_RENDER_KEY) } returns "true"
        coEvery { preferencesRepository.getValue(EmailDetailsViewModel.DISPLAY_IMAGES_KEY) } returns "false"

        // When
        viewModel.loadEmail(emailId)

        // Then
        assertEquals(email, viewModel.uiState.value.email)
    }

    @Test
    fun `loadEmail(emailId) sets uiState email to null when emailId is invalid`() = runTest {
        // Given
        val emailId = "123"

        coEvery { emailRepository.getEmailById(emailId) } returns null
        coEvery { preferencesRepository.getValue(EmailDetailsViewModel.HTML_RENDER_KEY) } returns "true"
        coEvery { preferencesRepository.getValue(EmailDetailsViewModel.DISPLAY_IMAGES_KEY) } returns "false"

        // When
        viewModel.loadEmail(emailId)

        // Then
        assertEquals(null, viewModel.uiState.value.email)
    }
}