package volovyk.guerrillamail.ui.assigned

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import volovyk.MainCoroutineRule
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.util.State

class AssignedEmailViewModelTest {

    private lateinit var viewModel: AssignedEmailViewModel
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
        viewModel = AssignedEmailViewModel(emailRepository)
    }

    @Test
    fun `setEmailAddress calls emailRepository`() = runTest {
        val newAddress = "test@example.com"

        viewModel.setEmailAddress(newAddress)

        coVerify { emailRepository.setEmailAddress(newAddress) }
    }
}