package volovyk.guerrillamail.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify
import volovyk.MainCoroutineRule
import volovyk.guerrillamail.data.EmailRepository
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var emailRepository: EmailRepository

    private lateinit var assignedEmailFlow: MutableStateFlow<String?>
    private lateinit var stateFlow: MutableStateFlow<RemoteEmailDatabase.State>

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        emailRepository = mock(EmailRepository::class.java)

        assignedEmailFlow = MutableStateFlow(null)
        stateFlow = MutableStateFlow(RemoteEmailDatabase.State.Loading)

        `when`(emailRepository.observeAssignedEmail()).thenReturn(assignedEmailFlow)
        `when`(emailRepository.observeState()).thenReturn(stateFlow)

        viewModel = MainViewModel(emailRepository)
    }

    @Test
    fun `viewModel emits correct uiState`() = runTest {
        // Create an empty collector for the StateFlow
        val uiStateCollectionJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        val defaultUiState = UiState()

        // Assert default UiState
        assertEquals(defaultUiState, viewModel.uiState.value)

        val emailAddress = "test@example.com"

        val expectedUiState =
            UiState(assignedEmail = emailAddress, state = RemoteEmailDatabase.State.Success)

        assignedEmailFlow.update { emailAddress }
        stateFlow.update { RemoteEmailDatabase.State.Success }

        // Assert new UiState is emitted
        assertEquals(expectedUiState, viewModel.uiState.value)

        uiStateCollectionJob.cancel()
    }

    @Test
    fun `setEmailAddress calls emailRepository`() = runTest {
        val newAddress = "test@example.com"

        viewModel.setEmailAddress(newAddress)

        verify(emailRepository).setEmailAddress(newAddress)
    }
}
