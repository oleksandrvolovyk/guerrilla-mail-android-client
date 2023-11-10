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
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.util.State
import javax.inject.Inject

data class UiState(
    val state: State = State.Loading,
    val mainRemoteEmailDatabaseIsAvailable: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {

    init {
        Timber.d("init ${hashCode()}")
    }

    val uiState: StateFlow<UiState> =
        combine(
            emailRepository.observeState(),
            emailRepository.observeMainRemoteEmailDatabaseAvailability()
        ) { state, mainRemoteEmailDatabaseAvailability ->
            UiState(state, mainRemoteEmailDatabaseAvailability)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState()
        )

    fun retryConnectingToMainDatabase() {
        viewModelScope.launch {
            emailRepository.retryConnectingToMainDatabase()
        }
    }
}