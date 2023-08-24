package volovyk.guerrillamail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.data.EmailRepository
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import javax.inject.Inject

data class UiState(
    val assignedEmail: String? = null,
    val state: RemoteEmailDatabase.State = RemoteEmailDatabase.State.Loading
)

@HiltViewModel
class MainViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {

    init {
        Timber.d("init ${hashCode()}")
    }

    val uiState: StateFlow<UiState> =
        combine(
            emailRepository.observeAssignedEmail(),
            emailRepository.observeState()
        ) { assignedEmail, state ->
            UiState(assignedEmail, state)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState()
        )

    fun setEmailAddress(newAddress: String) {
        viewModelScope.launch {
            emailRepository.setEmailAddress(newAddress)
        }
    }
}