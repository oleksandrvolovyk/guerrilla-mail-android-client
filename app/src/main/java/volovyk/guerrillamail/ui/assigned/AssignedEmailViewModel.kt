package volovyk.guerrillamail.ui.assigned

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.data.emails.EmailRepository
import javax.inject.Inject

data class AssignedEmailUiState(
    val emailUsername: String? = null,
    val emailDomain: String? = null,
    val isGetNewAddressButtonVisible: Boolean = false
)

@HiltViewModel
class AssignedEmailViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {

    init {
        Timber.d("init ${hashCode()}")
    }

    private val _uiState = MutableStateFlow(AssignedEmailUiState())
    val uiState: StateFlow<AssignedEmailUiState> = _uiState.asStateFlow()

    private val assignedEmailFlow = emailRepository.observeAssignedEmail().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    init {
        assignedEmailFlow
            .onEach { assignedEmail ->
                _uiState.update {
                    it.copy(
                        emailUsername = assignedEmail?.emailUsernamePart(),
                        emailDomain = assignedEmail?.emailDomainPart()
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun setEmailAddress(newAddress: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGetNewAddressButtonVisible = false) }
            if (!emailRepository.setEmailAddress(newAddress)) {
                // If the operation is unsuccessful, revert emailUsername to previous assigned email
                _uiState.update { it.copy(emailUsername = assignedEmailFlow.value?.emailUsernamePart()) }
            }
        }
    }

    fun userChangedEmailUsername(newEmailUsername: String) {
        _uiState.update {
            it.copy(
                emailUsername = newEmailUsername,
                isGetNewAddressButtonVisible = newEmailUsername != assignedEmailFlow.value?.emailUsernamePart()
            )
        }
    }

    private fun String.emailUsernamePart(): String = this.substringBefore("@")
    private fun String.emailDomainPart(): String = this.substringAfter("@")
}