package volovyk.guerrillamail.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.ads.AdManager
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.SideEffect
import javax.inject.Inject

data class SelectableItem<T>(
    val selected: Boolean = false,
    val item: T
)

data class EmailListUiState(
    val emails: List<SelectableItem<Email>> = emptyList(),
    val ads: List<NativeAd> = emptyList()
) {
    val selectedEmailsCount = emails.count { it.selected }
}

@HiltViewModel
class EmailListViewModel @Inject constructor(
    private val emailRepository: EmailRepository,
    private val adManager: AdManager
) : ViewModel() {

    init {
        Timber.d("init ${hashCode()}")
    }

    private val selectedEmailIds: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    val uiState: StateFlow<EmailListUiState> = combine(
        emailRepository.observeEmails(),
        selectedEmailIds,
        adManager.ads
    ) { emails, selectedEmailIds, ads ->
        EmailListUiState(
            emails = emails
                .map { email -> SelectableItem(selected = email.id in selectedEmailIds, email) },
            ads = ads
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EmailListUiState()
    )

    private val _sideEffectChannel = Channel<SideEffect>(capacity = Channel.BUFFERED)
    val sideEffectFlow: Flow<SideEffect>
        get() = _sideEffectChannel.receiveAsFlow()

    fun toggleEmailSelection(email: Email) = selectedEmailIds.update {
        if (email.id in it) {
            it - email.id
        } else {
            it + email.id
        }
    }

    fun clearSelectedEmails() = selectedEmailIds.update { emptyList() }

    fun toggleSelectAllEmails() = if (selectedEmailIds.value.size == uiState.value.emails.size) {
        clearSelectedEmails()
    } else {
        selectedEmailIds.update { uiState.value.emails.map { it.item.id } }
    }

    fun deleteSelectedEmails() {
        _sideEffectChannel.trySend(
            SideEffect.ConfirmAction(
                R.string.confirm_deleting_selected_emails,
                selectedEmailIds.value.size.toString()
            ) {
                viewModelScope.launch {
                    emailRepository.deleteEmails(selectedEmailIds.value)
                    selectedEmailIds.update { emptyList() }
                }
            }
        )
    }

    suspend fun loadAd(adPosition: Int) = adManager.loadAd(adPosition)
}