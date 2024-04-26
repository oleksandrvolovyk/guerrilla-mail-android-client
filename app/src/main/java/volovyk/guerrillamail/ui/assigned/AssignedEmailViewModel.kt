package volovyk.guerrillamail.ui.assigned

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.EmailRepositoryException
import volovyk.guerrillamail.ui.SideEffect
import volovyk.guerrillamail.ui.util.EmailValidator
import javax.inject.Inject

data class AssignedEmailUiState(
    val emailUsername: String? = null,
    val emailDomain: String? = null,
    val isGetNewAddressButtonVisible: Boolean = false
)

@HiltViewModel
class AssignedEmailViewModel @Inject constructor(
    private val emailRepository: EmailRepository,
    private val emailValidator: EmailValidator
) : ViewModel() {

    init {
        Timber.d("init ${hashCode()}")
    }

    private val _uiState = MutableStateFlow(AssignedEmailUiState())
    val uiState: StateFlow<AssignedEmailUiState> = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<SideEffect>(capacity = Channel.BUFFERED)
    val sideEffectFlow: Flow<SideEffect>
        get() = _sideEffectChannel.receiveAsFlow()

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

    fun getNewEmailAddress() {
        val newAddress = uiState.value.emailUsername?.let {
            "${uiState.value.emailUsername}@${uiState.value.emailDomain}"
        }

        if (newAddress == null || !emailValidator.isValidEmailAddress(newAddress)) {
            _sideEffectChannel.trySend(SideEffect.ShowToast(R.string.email_invalid))
            return
        }

        _sideEffectChannel.trySend(
            SideEffect.ConfirmAction(
                R.string.confirm_getting_new_address,
                newAddress
            ) {
                viewModelScope.launch {
                    _uiState.update { it.copy(isGetNewAddressButtonVisible = false) }
                    try {
                        emailRepository.setEmailAddress(newAddress)
                    } catch (e: EmailRepositoryException.EmailAddressAssignmentException) {
                        // If the operation is unsuccessful, revert emailUsername to previous assigned email
                        _uiState.update { it.copy(emailUsername = assignedEmailFlow.value?.emailUsernamePart()) }
                    }
                }
            })
    }

    fun userChangedEmailUsername(newEmailUsername: String) {
        _uiState.update {
            it.copy(
                emailUsername = newEmailUsername,
                isGetNewAddressButtonVisible = newEmailUsername != assignedEmailFlow.value?.emailUsernamePart()
            )
        }
    }

    fun copyEmailAddressToClipboard() {
        assignedEmailFlow.value?.let { assignedEmail ->
            _sideEffectChannel.trySend(SideEffect.CopyTextToClipboard(text = assignedEmail))
            _sideEffectChannel.trySend(SideEffect.ShowToast(R.string.email_in_clipboard))
        }
    }

    private fun String.emailUsernamePart(): String = this.substringBefore("@")
    private fun String.emailDomainPart(): String = this.substringAfter("@")
}