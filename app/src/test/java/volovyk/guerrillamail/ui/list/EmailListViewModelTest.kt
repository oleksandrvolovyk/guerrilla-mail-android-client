package volovyk.guerrillamail.ui.list

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import volovyk.MainCoroutineRule
import volovyk.guerrillamail.data.EmailRepository
import volovyk.guerrillamail.data.model.Email

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
        emailRepository = Mockito.mock(EmailRepository::class.java)

        emailFlow = MutableStateFlow(emptyList())

        `when`(emailRepository.observeEmails()).thenReturn(emailFlow)

        viewModel = EmailListViewModel(emailRepository)
    }

    @Test
    fun `viewModel emits correct uiState`() = runTest {
        // Create an empty collector for the StateFlow
        val uiStateCollectionJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        val defaultUiState = EmailListUiState()

        // Assert default UiState
        assertEquals(defaultUiState, viewModel.uiState.value)

        val email = Email(0, "", "", "", "", false)
        val expectedUiState = EmailListUiState(listOf(email))

        emailFlow.emit(listOf(email))

        // Assert new UiState is emitted
        assertEquals(expectedUiState, viewModel.uiState.value)

        uiStateCollectionJob.cancel()
    }

    @Test
    fun `deleteEmail calls emailRepository`() = runTest {
        // Given
        val emailToDelete = Email(0, "", "", "", "", false)

        // When
        viewModel.deleteEmail(emailToDelete)

        // Then
        verify(emailRepository).deleteEmail(emailToDelete)
    }

    @Test
    fun `deleteAllEmails calls emailRepository`() = runTest {
        // When
        viewModel.deleteAllEmails()

        // Then
        verify(emailRepository).deleteAllEmails()
    }
}