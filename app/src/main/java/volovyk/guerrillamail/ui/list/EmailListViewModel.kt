package volovyk.guerrillamail.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import volovyk.guerrillamail.data.EmailRepository
import volovyk.guerrillamail.data.model.Email
import javax.inject.Inject

data class EmailListUiState(
    val emails: List<Email> = emptyList()
)

@HiltViewModel
class EmailListViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {

    val uiState: StateFlow<EmailListUiState> = emailRepository.observeEmails()
        .map { EmailListUiState(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            EmailListUiState()
        )

    fun deleteEmail(email: Email) {
        viewModelScope.launch {
            emailRepository.deleteEmail(email)
        }
    }

    fun deleteAllEmails() {
        viewModelScope.launch {
            emailRepository.deleteAllEmails()
        }
    }
}