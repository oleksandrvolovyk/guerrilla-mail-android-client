package volovyk.guerrillamail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import volovyk.guerrillamail.data.emails.model.EmailRepositoryException
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
    }

    val uiState: StateFlow<UiState> = emailRepository.observeState()
        .map {
            UiState(
                isLoading = it.isLoading,
                isMainRemoteEmailDatabaseAvailable = it.isMainRemoteEmailDatabaseAvailable
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState()
        )

    val sideEffectFlow: Flow<SideEffect> = emailRepository.observeErrors()
        .receiveAsFlow()
        .map {
            when (it) {
                is EmailRepositoryException.EmailAddressAssignmentException ->
                    SideEffect.ShowToast(R.string.email_address_assignment_failure, it.message)

                is EmailRepositoryException.EmailFetchException ->
                    SideEffect.ShowToast(R.string.email_fetch_failure, it.message)
            }
        }

    fun retryConnectingToMainDatabase() {
        viewModelScope.launch {
            emailRepository.retryConnectingToMainDatabase()
        }
    }
}