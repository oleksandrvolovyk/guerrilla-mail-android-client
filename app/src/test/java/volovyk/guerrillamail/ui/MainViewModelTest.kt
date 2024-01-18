package volovyk.guerrillamail.ui

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
import volovyk.guerrillamail.util.State

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var emailRepository: EmailRepository

    private lateinit var stateFlow: MutableStateFlow<State>
    private lateinit var mainRemoteEmailDatabaseAvailability: MutableStateFlow<Boolean>

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        emailRepository = mockk<EmailRepository>(relaxed = true)

        stateFlow = MutableStateFlow(State.Loading)
        mainRemoteEmailDatabaseAvailability = MutableStateFlow(true)

        every { emailRepository.observeState() } returns stateFlow
        every { emailRepository.observeMainRemoteEmailDatabaseAvailability() } returns mainRemoteEmailDatabaseAvailability
        viewModel = MainViewModel(emailRepository)
    }

    @Test
    fun `viewModel emits correct uiState`() = runTest {
        // Create an empty collector for the StateFlow
        val uiStateCollectionJob = backgroundScope.launch(StandardTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        val defaultUiState = UiState()

        // Assert default UiState
        assertEquals(defaultUiState, viewModel.uiState.value)

        stateFlow.update { State.Success }
        mainRemoteEmailDatabaseAvailability.update { false }

        advanceUntilIdle()

        val expectedUiState = UiState(isLoading = false, isMainRemoteEmailDatabaseAvailable = false)

        // Assert new UiState is emitted
        assertEquals(expectedUiState, viewModel.uiState.value)

        uiStateCollectionJob.cancel()
    }

    @Test
    fun `retryConnectingToMainDatabase calls emailRepository`() = runTest {
        viewModel.retryConnectingToMainDatabase()

        coVerify { emailRepository.retryConnectingToMainDatabase() }
    }
}