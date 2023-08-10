package volovyk.guerrillamail.ui.specific

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import volovyk.guerrillamail.data.EmailRepository
import volovyk.guerrillamail.data.model.Email
import javax.inject.Inject

data class SpecificEmailUiState(
    val email: Email? = null
)

@HiltViewModel
class SpecificEmailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val emailRepository: EmailRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpecificEmailUiState())
    val uiState: StateFlow<SpecificEmailUiState> = _uiState.asStateFlow()

    init {
        val emailId: Int? = savedStateHandle["emailId"]

        emailId?.let {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        email = emailRepository.getEmailById(emailId)
                    )
                }
            }
        }
    }

}