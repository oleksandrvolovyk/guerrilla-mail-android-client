package volovyk.guerrillamail.ui

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
import volovyk.MainCoroutineRule
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.util.State

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var emailRepository: EmailRepository

    private lateinit var assignedEmailFlow: MutableStateFlow<String?>
    private lateinit var stateFlow: MutableStateFlow<State>
    private lateinit var mainRemoteEmailDatabaseAvailability: MutableStateFlow<Boolean>

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        emailRepository = mockk<EmailRepository>(relaxed = true)

        assignedEmailFlow = MutableStateFlow(null)
        stateFlow = MutableStateFlow(State.Loading)
        mainRemoteEmailDatabaseAvailability = MutableStateFlow(true)

        every { emailRepository.observeAssignedEmail() } returns assignedEmailFlow
        every { emailRepository.observeState() } returns stateFlow
        every { emailRepository.observeMainRemoteEmailDatabaseAvailability() } returns mainRemoteEmailDatabaseAvailability
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
            UiState(assignedEmail = emailAddress, state = State.Success)

        assignedEmailFlow.update { emailAddress }
        stateFlow.update { State.Success }

        // Assert new UiState is emitted
        assertEquals(expectedUiState, viewModel.uiState.value)

        uiStateCollectionJob.cancel()
    }

    @Test
    fun `setEmailAddress calls emailRepository`() = runTest {
        val newAddress = "test@example.com"

        viewModel.setEmailAddress(newAddress)

        coVerify { emailRepository.setEmailAddress(newAddress) }
    }
}
