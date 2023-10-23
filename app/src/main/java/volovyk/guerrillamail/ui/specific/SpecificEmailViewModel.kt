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
import timber.log.Timber
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.preferences.PreferencesRepository
import javax.inject.Inject

data class SpecificEmailUiState(
    val email: Email? = null,
    val renderHtml: Boolean = true
)

@HiltViewModel
class SpecificEmailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val emailRepository: EmailRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpecificEmailUiState())
    val uiState: StateFlow<SpecificEmailUiState> = _uiState.asStateFlow()

    init {
        Timber.d("init ${hashCode()}")
        val emailId: String? = savedStateHandle["emailId"]

        emailId?.let {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        email = emailRepository.getEmailById(emailId),
                        renderHtml = preferencesRepository.getValue(HTML_RENDER_KEY).toBoolean()
                    )
                }
            }
        }
    }

    fun setHtmlRender(render: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setValue(HTML_RENDER_KEY, render.toString())
            _uiState.update {
                it.copy(renderHtml = render)
            }
        }
    }

    companion object {
        const val HTML_RENDER_KEY = "html_render"
    }
}