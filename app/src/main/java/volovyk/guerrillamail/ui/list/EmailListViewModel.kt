package volovyk.guerrillamail.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.SideEffect
import javax.inject.Inject

data class EmailListUiState(
    val emails: List<Email> = emptyList()
)

@HiltViewModel
class EmailListViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {

    init {
        Timber.d("init ${hashCode()}")
    }

    val uiState: StateFlow<EmailListUiState> = emailRepository.observeEmails()
        .map { EmailListUiState(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            EmailListUiState()
        )

    private val _sideEffectChannel = Channel<SideEffect>(capacity = Channel.BUFFERED)
    val sideEffectFlow: Flow<SideEffect>
        get() = _sideEffectChannel.receiveAsFlow()

    fun deleteEmail(email: Email) {
        _sideEffectChannel.trySend(
            SideEffect.ConfirmAction(R.string.confirm_deleting_email) {
                viewModelScope.launch { emailRepository.deleteEmail(email) }
            }
        )
    }

    fun deleteAllEmails() {
        _sideEffectChannel.trySend(
            SideEffect.ConfirmAction(R.string.confirm_deleting_all_emails) {
                viewModelScope.launch { emailRepository.deleteAllEmails() }
            }
        )
    }
}