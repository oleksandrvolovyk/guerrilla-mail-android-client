package volovyk.guerrillamail.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.preferences.PreferencesRepository
import volovyk.guerrillamail.ui.SideEffect
import javax.inject.Inject

data class EmailDetailsUiState(
    val email: Email? = null,
    val renderHtml: Boolean = true,
    val displayImages: Boolean = false
)

@HiltViewModel
class EmailDetailsViewModel @Inject constructor(
    private val emailRepository: EmailRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailDetailsUiState())
    val uiState: StateFlow<EmailDetailsUiState> = _uiState.asStateFlow()

    private val _sideEffectChannel = Channel<SideEffect>(capacity = Channel.BUFFERED)
    val sideEffectFlow: Flow<SideEffect>
        get() = _sideEffectChannel.receiveAsFlow()

    fun loadEmail(emailId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                email = emailRepository.getEmailById(emailId),
                renderHtml = preferencesRepository.getValue(HTML_RENDER_KEY).toBoolean(),
                displayImages = preferencesRepository.getValue(DISPLAY_IMAGES_KEY).toBoolean()
            )
        }
    }

    fun setHtmlRender(render: Boolean) = viewModelScope.launch {
        preferencesRepository.setValue(HTML_RENDER_KEY, render.toString())
        _uiState.update {
            it.copy(renderHtml = render)
        }
    }

    fun setDisplayImages(value: Boolean) = viewModelScope.launch {
        preferencesRepository.setValue(DISPLAY_IMAGES_KEY, value.toString())
        _uiState.update {
            it.copy(displayImages = value)
        }
    }

    fun copySenderAddressToClipboard() {
        uiState.value.email?.let { email ->
            _sideEffectChannel.trySend(SideEffect.CopyTextToClipboard(text = email.from))
            _sideEffectChannel.trySend(SideEffect.ShowToast(R.string.senders_email_in_clipboard))
        }
    }

    companion object {
        const val HTML_RENDER_KEY = "html_render"
        const val DISPLAY_IMAGES_KEY = "display_images"
    }
}