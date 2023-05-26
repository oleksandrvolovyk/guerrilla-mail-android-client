package volovyk.guerrillamail.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import volovyk.guerrillamail.data.SingleEvent
import volovyk.guerrillamail.data.model.Email
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuerrillaEmailDatabase @Inject constructor() {

    private val _assignedEmail = MutableLiveData<String?>()
    val assignedEmail: LiveData<String?> = _assignedEmail

    val emails: Flow<List<Email>> = flow {
        while (true) {
            if (!gotEmailAssigned) {
                _refreshing.postValue(true)
                emailAddress
                _refreshing.postValue(false)
                delay(EMAIL_ASSIGNMENT_INTERVAL) // Suspends the coroutine for some time
            } else if (needNewEmailAddress) {
                _refreshing.postValue(true)
                makeSetEmailAddressRequest(requestedEmailAddress)
                _refreshing.postValue(false)
                delay(EMAIL_ASSIGNMENT_INTERVAL) // Suspends the coroutine for some time
            } else {
                _refreshing.postValue(true)
                val emailsList = checkForNewEmails()
                emit(emailsList)
                _refreshing.postValue(false)
                delay(REFRESH_INTERVAL) // Suspends the coroutine for some time
            }
        }
    }.flowOn(Dispatchers.IO)

    private val _refreshing = MutableLiveData(false)
    val refreshing: LiveData<Boolean> = _refreshing
    private val _errorLiveData = MutableLiveData<SingleEvent<String>>()
    val errorLiveData: LiveData<SingleEvent<String>> = _errorLiveData

    private var sidToken: String? = null
    private var seq = 0
    private val apiInterface: APIInterface = APIClient.client.create(APIInterface::class.java)

    private var gotEmailAssigned = false
    private var needNewEmailAddress = false
    private var requestedEmailAddress: String = ""

    companion object {
        private const val REFRESH_INTERVAL = 5000L // 5 seconds
        private const val EMAIL_ASSIGNMENT_INTERVAL = 1000L // 1 second, interval between attempts

        // to get an email address
        private const val SITE = "guerrillamail.com"
        private const val LANG = "en"
    }

    fun setEmailAddress(requestedEmailAddress: String) {
        needNewEmailAddress = true
        _assignedEmail.postValue(null)
        this.requestedEmailAddress = requestedEmailAddress
    }

    private fun makeSetEmailAddressRequest(requestedEmailAddress: String) {
        val call = apiInterface
            .setEmailAddress(
                sidToken,
                LANG,
                SITE,
                requestedEmailAddress
            )

        try {
            val response = call!!.execute()

            val setEmailAddressResponse = response.body()
            if (response.isSuccessful && setEmailAddressResponse != null) {
                sidToken = setEmailAddressResponse.sidToken
                _assignedEmail.postValue(setEmailAddressResponse.emailAddress)
                needNewEmailAddress = false
                gotEmailAssigned = true
            } else {
                setError("Something went wrong!")
            }
        } catch (e: IOException) {
            e.localizedMessage?.let { setError(it) }
        } catch (e: RuntimeException) {
            e.localizedMessage?.let { setError(it) }
        }
    }

    private val emailAddress: Unit
        get() {
            val call = apiInterface.emailAddress

            try {
                val response = call!!.execute()

                val getEmailAddressResponse = response.body()
                if (response.isSuccessful && getEmailAddressResponse != null) {
                    sidToken = getEmailAddressResponse.sidToken
                    _assignedEmail.postValue(getEmailAddressResponse.emailAddress)
                    gotEmailAssigned = true
                } else {
                    setError("Something went wrong!")
                }
            } catch (e: IOException) {
                e.localizedMessage?.let { setError(it) }
            } catch (e: RuntimeException) {
                e.localizedMessage?.let { setError(it) }
            }
        }

    private fun checkForNewEmails(): List<Email> {
        val call = apiInterface.checkForNewEmails(sidToken, seq)

        try {
            val response = call!!.execute()

            val checkForNewEmailsResponse = response.body()
            if (response.isSuccessful && checkForNewEmailsResponse != null) {
                sidToken = checkForNewEmailsResponse.sidToken
                if (!checkForNewEmailsResponse.emails.isNullOrEmpty()) {
                    return fetchAllEmails(checkForNewEmailsResponse.emails)
                }
            } else {
                setError("Something went wrong!")
            }
        } catch (e: IOException) {
            e.localizedMessage?.let { setError(it) }
        } catch (e: RuntimeException) {
            e.localizedMessage?.let { setError(it) }
        }
        return emptyList()
    }

    private fun fetchAllEmails(emailsList: List<Email>): List<Email> {
        val fetchedEmailsList: MutableList<Email> = mutableListOf()
        for (email in emailsList) {
            val call = apiInterface.fetchEmail(sidToken, email.id)
            try {
                val response = call!!.execute()

                val fullEmail = response.body()
                if (response.isSuccessful && fullEmail != null) {
                    fullEmail.body = formatEmailBody(fullEmail.body)
                    fetchedEmailsList.add(fullEmail)
                    seq = seq.coerceAtLeast(fullEmail.id)
                } else {
                    setError("Something went wrong!")
                }
            } catch (e: IOException) {
                e.localizedMessage?.let { setError(it) }
            } catch (e: RuntimeException) {
                e.localizedMessage?.let { setError(it) }
            }

        }
        return fetchedEmailsList
    }

    private fun formatEmailBody(body: String): String {
        return body.replace("\\r\\n".toRegex(), "<br>")
    }

    private fun setError(errorMessage: String) {
        _errorLiveData.postValue(SingleEvent(errorMessage))
    }
}