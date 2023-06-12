package volovyk.guerrillamail.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import volovyk.guerrillamail.data.EmailRepository
import volovyk.guerrillamail.data.SingleEvent
import volovyk.guerrillamail.data.model.Email
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {
    val assignedEmail: LiveData<String?> = emailRepository.assignedEmail
    private val _emails: MutableLiveData<List<Email>> = MutableLiveData()
    val emails: LiveData<List<Email>> = _emails
    val refreshing: LiveData<Boolean> = emailRepository.refreshing
    val errorLiveData: LiveData<SingleEvent<String>> = emailRepository.errorLiveData

    init {
        viewModelScope.launch {
            emailRepository.emails.collect { emails ->
                _emails.postValue(emails)
            }
        }
    }

    fun setEmailAddress(newAddress: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                emailRepository.setEmailAddress(newAddress)
            }
        }
    }

    fun deleteEmail(email: Email?) {
        emailRepository.deleteEmail(email)
    }
}