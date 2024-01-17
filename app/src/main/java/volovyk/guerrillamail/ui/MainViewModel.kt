package volovyk.guerrillamail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.data.emails.exception.EmailFetchException
import volovyk.guerrillamail.util.State
import javax.inject.Inject

data class UiState(
    val isLoading: Boolean = true,
    val isMainRemoteEmailDatabaseAvailable: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {

    init {
        Timber.d("init ${hashCode()}")

        emailRepository.observeState()
            .filter { it is State.Failure }
            .map { (it as State.Failure).error }
            .onEach {
                when (it) {
                    is EmailAddressAssignmentException -> _sideEffectChannel.trySend(
                        SideEffect.ShowToast(R.string.email_address_assignment_failure, it.message)
                    )

                    is EmailFetchException -> _sideEffectChannel.trySend(
                        SideEffect.ShowToast(R.string.email_fetch_failure, it.message)
                    )

                    else -> _sideEffectChannel.trySend(
                        SideEffect.ShowToast(R.string.common_failure)
                    )
                }
            }.launchIn(viewModelScope)
    }

    val uiState: StateFlow<UiState> =
        combine(
            emailRepository.observeState(),
            emailRepository.observeMainRemoteEmailDatabaseAvailability()
        ) { state, mainRemoteEmailDatabaseAvailability ->
            UiState(
                isLoading = state is State.Loading,
                isMainRemoteEmailDatabaseAvailable = mainRemoteEmailDatabaseAvailability
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState()
        )

    private val _sideEffectChannel = Channel<SideEffect>(capacity = Channel.BUFFERED)
    val sideEffectFlow: Flow<SideEffect>
        get() = _sideEffectChannel.receiveAsFlow()

    fun retryConnectingToMainDatabase() {
        viewModelScope.launch {
            emailRepository.retryConnectingToMainDatabase()
        }
    }
}