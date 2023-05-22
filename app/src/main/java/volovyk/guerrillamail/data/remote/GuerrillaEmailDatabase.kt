package volovyk.guerrillamail.data.remote

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import volovyk.guerrillamail.data.SingleEvent
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.pojo.CheckForNewEmailsResponse
import volovyk.guerrillamail.data.remote.pojo.GetEmailAddressResponse
import volovyk.guerrillamail.data.remote.pojo.SetEmailAddressResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuerrillaEmailDatabase @Inject constructor() {

    private val _assignedEmail = MutableLiveData<String?>()
    val assignedEmail: LiveData<String?> = _assignedEmail
    private val _emails = MutableLiveData<List<Email>>(ArrayList())
    val emails: LiveData<List<Email>> = _emails
    private val _refreshing = MutableLiveData(false)
    val refreshing: LiveData<Boolean> = _refreshing
    private val _errorLiveData = MutableLiveData<SingleEvent<String>>()
    val errorLiveData: LiveData<SingleEvent<String>> = _errorLiveData

    private var sidToken: String? = null
    private var seq = 0
    private val apiInterface: APIInterface = APIClient.client.create(APIInterface::class.java)

    private val mHandler: Handler = Handler()
    private var gotEmailAssigned = false
    private var needNewEmailAddress = false
    private var requestedEmailAddress: String? = null
    private var refresher: Runnable = object : Runnable {
        override fun run() {
            try {
                _refreshing.postValue(true)
                if (!gotEmailAssigned) {
                    emailAddress
                } else if (needNewEmailAddress) {
                    makeSetEmailAddressRequest(requestedEmailAddress)
                } else {
                    checkForNewEmails()
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(this, REFRESH_INTERVAL.toLong())
            }
        }
    }

    init {
        startRepeatingTask()
    }

    companion object {
        private const val REFRESH_INTERVAL = 5000 // 5 seconds
        private const val SITE = "guerrillamail.com"
        private const val LANG = "en"
    }

    private fun startRepeatingTask() {
        refresher.run()
    }

    fun setEmailAddress(requestedEmailAddress: String?) {
        _refreshing.postValue(true)
        needNewEmailAddress = true
        _assignedEmail.postValue(null)
        this.requestedEmailAddress = requestedEmailAddress
    }

    private fun makeSetEmailAddressRequest(requestedEmailAddress: String?) {
        val call = apiInterface
            .setEmailAddress(
                sidToken,
                LANG,
                SITE,
                requestedEmailAddress
            )
        call!!.enqueue(object : Callback<SetEmailAddressResponse?> {
            override fun onResponse(
                call: Call<SetEmailAddressResponse?>,
                response: Response<SetEmailAddressResponse?>
            ) {
                val setEmailAddressResponse = response.body()
                if (setEmailAddressResponse != null) {
                    if (setEmailAddressResponse.sidToken != null) {
                        sidToken = setEmailAddressResponse.sidToken
                    }
                    if (setEmailAddressResponse.emailAddress != null) {
                        val newEmailAddress = setEmailAddressResponse.emailAddress
                        _assignedEmail.postValue(newEmailAddress)
                        needNewEmailAddress = false
                        gotEmailAssigned = true
                    }
                    _refreshing.postValue(false)
                }
            }

            override fun onFailure(
                call: Call<SetEmailAddressResponse?>,
                t: Throwable
            ) {
                t.localizedMessage?.let { setError(it) }
            }
        })
    }

    private val emailAddress: Unit
        get() {
            val call = apiInterface.emailAddress
            call!!.enqueue(object : Callback<GetEmailAddressResponse?> {
                override fun onResponse(
                    call: Call<GetEmailAddressResponse?>,
                    response: Response<GetEmailAddressResponse?>
                ) {
                    val getEmailAddressResponse = response.body()
                    if (getEmailAddressResponse != null) {
                        sidToken = getEmailAddressResponse.sidToken
                        _assignedEmail.postValue(getEmailAddressResponse.emailAddress)
                        gotEmailAssigned = true
                        _refreshing.postValue(false)
                    }
                }

                override fun onFailure(call: Call<GetEmailAddressResponse?>, t: Throwable) {
                    t.localizedMessage?.let { setError(it) }
                }
            })
        }

    private fun checkForNewEmails() {
        val call = apiInterface.checkForNewEmails(sidToken, seq)
        call!!.enqueue(object : Callback<CheckForNewEmailsResponse?> {
            override fun onResponse(
                call: Call<CheckForNewEmailsResponse?>,
                response: Response<CheckForNewEmailsResponse?>
            ) {
                val checkForNewEmailsResponse = response.body()
                if (checkForNewEmailsResponse != null) {
                    if (checkForNewEmailsResponse.sidToken != null) {
                        sidToken = checkForNewEmailsResponse.sidToken
                    }
                    if (checkForNewEmailsResponse.emails != null) {
                        if (checkForNewEmailsResponse.emails.isNotEmpty()) {
                            fetchAllEmails(checkForNewEmailsResponse.emails)
                        }
                        _refreshing.postValue(false)
                    }
                }
            }

            override fun onFailure(
                call: Call<CheckForNewEmailsResponse?>,
                t: Throwable
            ) {
                t.localizedMessage?.let { setError(it) }
            }
        })
    }

    private fun fetchAllEmails(emailsList: List<Email?>?) {
        for (email in emailsList!!) {
            val call = apiInterface.fetchEmail(sidToken, email?.id)
            call!!.enqueue(object : Callback<Email?> {
                override fun onResponse(
                    call: Call<Email?>,
                    response: Response<Email?>
                ) {
                    val fullEmail = response.body()
                    if (fullEmail != null) {
                        fullEmail.body = formatEmailBody(fullEmail.body)
                        _emails.value = listOf(fullEmail)
                        seq = seq.coerceAtLeast(fullEmail.id)
                    }
                }

                override fun onFailure(call: Call<Email?>, t: Throwable) {
                    t.localizedMessage?.let { setError(it) }
                }
            })
        }
    }

    private fun formatEmailBody(body: String): String {
        return body.replace("\\r\\n".toRegex(), "<br>")
    }

    private fun setError(errorMessage: String) {
        _errorLiveData.value = SingleEvent(errorMessage)
    }
}