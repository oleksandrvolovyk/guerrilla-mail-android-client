package volovyk.guerrillamail.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import volovyk.guerrillamail.data.SingleEvent
import volovyk.guerrillamail.data.model.Email
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuerrillaEmailDatabase @Inject constructor(private val guerrillaMailApiInterface: GuerrillaMailApiInterface) :
    RemoteEmailDatabase {

    private val _assignedEmail = MutableLiveData<String?>()
    override val assignedEmail: LiveData<String?> = _assignedEmail

    override val emails: Flow<List<Email>> = flow {
        while (true) {
            if (!gotEmailAssigned) {
                _refreshing.postValue(true)
                val email = getEmailAddress()
                _assignedEmail.postValue(email)
                if (email != null) {
                    gotEmailAssigned = true
                }
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
    override val refreshing: LiveData<Boolean> = _refreshing
    private val _errorLiveData = MutableLiveData<SingleEvent<String>>()
    override val errorLiveData: LiveData<SingleEvent<String>> = _errorLiveData

    private var sidToken: String? = null
    private var seq = 0

    fun getSidToken() = sidToken

    fun getSeq() = seq

    private var gotEmailAssigned = false

    companion object {
        private const val REFRESH_INTERVAL = 5000L // 5 seconds
        private const val EMAIL_ASSIGNMENT_INTERVAL = 1000L // 1 second, interval between attempts

        // to get an email address
        private const val SITE = "guerrillamail.com"
        private const val LANG = "en"
    }

    override fun setEmailAddress(requestedEmailAddress: String) {
        val assignedEmailAddress = makeSetEmailAddressRequest(requestedEmailAddress)

        _assignedEmail.postValue(assignedEmailAddress)
    }

    private fun makeSetEmailAddressRequest(requestedEmailAddress: String): String? {
        val call = guerrillaMailApiInterface
            .setEmailAddress(
                sidToken,
                LANG,
                SITE,
                requestedEmailAddress
            )

        val setEmailAddressResponse = call.executeAndCatchErrors()

        setEmailAddressResponse?.let {
            sidToken = setEmailAddressResponse.sidToken
            return setEmailAddressResponse.emailAddress
        }

        return null
    }

    private fun getEmailAddress(): String? {
        val call = guerrillaMailApiInterface.emailAddress

        val getEmailAddressResponse = call.executeAndCatchErrors()

        getEmailAddressResponse?.let {
            sidToken = getEmailAddressResponse.sidToken
            return getEmailAddressResponse.emailAddress
        }

        return null
    }

    private fun checkForNewEmails(): List<Email> {
        val call = guerrillaMailApiInterface.checkForNewEmails(sidToken, seq)

        val checkForNewEmailsResponse = call.executeAndCatchErrors()

        checkForNewEmailsResponse?.let {
            sidToken = checkForNewEmailsResponse.sidToken
            if (!checkForNewEmailsResponse.emails.isNullOrEmpty()) {
                return fetchAllEmails(checkForNewEmailsResponse.emails)
            }
        }

        return emptyList()
    }

    private fun fetchAllEmails(emailsList: List<Email>): List<Email> {
        val fetchedEmailsList: MutableList<Email> = mutableListOf()
        for (email in emailsList) {
            val call = guerrillaMailApiInterface.fetchEmail(sidToken, email.id)

            val fullEmail = call.executeAndCatchErrors()

            fullEmail?.let {
                fullEmail.body = formatEmailBody(fullEmail.body)
                fetchedEmailsList.add(fullEmail)
                seq = seq.coerceAtLeast(fullEmail.id)
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

    private fun <T> Call<T>.executeAndCatchErrors(): T? {
        return try {
            val response = this.execute()

            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                return responseBody
            } else {
                setError("Something went wrong!")
                return null
            }
        } catch (e: IOException) {
            e.localizedMessage?.let { setError(it) }
            null
        } catch (e: RuntimeException) {
            e.localizedMessage?.let { setError(it) }
            null
        }
    }
}