package volovyk.guerrillamail.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import volovyk.guerrillamail.data.EmailRepository
import volovyk.guerrillamail.data.SingleEvent
import volovyk.guerrillamail.data.model.Email
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val emailRepository: EmailRepository) :
    ViewModel() {
    val assignedEmail: LiveData<String?>? = emailRepository.assignedEmail
    val emails: LiveData<List<Email?>?>? = emailRepository.emails
    val refreshing: LiveData<Boolean> = emailRepository.refreshing
    val errorLiveData: LiveData<SingleEvent<String>> = emailRepository.errorLiveData

    fun setEmailAddress(newAddress: String?) {
        emailRepository.setEmailAddress(newAddress)
    }

    fun deleteEmail(email: Email?) {
        emailRepository.deleteEmail(email)
    }
}