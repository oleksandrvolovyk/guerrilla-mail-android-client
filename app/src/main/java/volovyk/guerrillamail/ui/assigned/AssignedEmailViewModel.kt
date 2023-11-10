package volovyk.guerrillamail.ui.assigned

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.util.State
import javax.inject.Inject

data class AssignedEmailUiState(
    val assignedEmail: String? = null,
    val state: State = State.Loading
)

@HiltViewModel
class AssignedEmailViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {

    init {
        Timber.d("init ${hashCode()}")
    }

    val uiState: StateFlow<AssignedEmailUiState> =
        combine(
            emailRepository.observeAssignedEmail(),
            emailRepository.observeState()
        ) { assignedEmail, state ->
            AssignedEmailUiState(assignedEmail, state)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AssignedEmailUiState()
        )

    fun setEmailAddress(newAddress: String) {
        viewModelScope.launch {
            emailRepository.setEmailAddress(newAddress)
        }
    }
}