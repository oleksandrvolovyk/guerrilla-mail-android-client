package volovyk.guerrillamail.ui.list

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import volovyk.MainCoroutineRule
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email

@OptIn(ExperimentalCoroutinesApi::class)
class EmailListViewModelTest {

    private lateinit var viewModel: EmailListViewModel
    private lateinit var emailRepository: EmailRepository

    private lateinit var emailFlow: MutableStateFlow<List<Email>>

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        emailRepository = mockk<EmailRepository>(relaxed = true)

        emailFlow = MutableStateFlow(emptyList())

        every { emailRepository.observeEmails() } returns emailFlow

        viewModel = EmailListViewModel(emailRepository)
    }

    @Test
    fun `viewModel emits correct uiState`() = runTest {
        // Create an empty collector for the StateFlow
        val uiStateCollectionJob = backgroundScope.launch(StandardTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        val defaultUiState = EmailListUiState()

        // Assert default UiState
        assertEquals(defaultUiState, viewModel.uiState.value)

        val email = Email("0", "", "", "", "", "", false)
        val expectedUiState = EmailListUiState(listOf(email))

        emailFlow.emit(listOf(email))

        advanceUntilIdle()

        // Assert new UiState is emitted
        assertEquals(expectedUiState, viewModel.uiState.value)

        uiStateCollectionJob.cancel()
    }

    @Test
    fun `deleteEmail calls emailRepository`() = runTest {
        // Given
        val emailToDelete = Email("0", "", "", "", "", "", false)

        // When
        viewModel.deleteEmail(emailToDelete)

        // Then
        coVerify { emailRepository.deleteEmail(emailToDelete) }
    }

    @Test
    fun `deleteAllEmails calls emailRepository`() = runTest {
        // When
        viewModel.deleteAllEmails()

        // Then
        coVerify { emailRepository.deleteAllEmails() }
    }
}