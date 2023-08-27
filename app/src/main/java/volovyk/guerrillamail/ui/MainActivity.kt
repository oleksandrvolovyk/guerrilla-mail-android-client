package volovyk.guerrillamail.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.ads.AdManager
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.remote.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.data.remote.exception.EmailFetchException
import volovyk.guerrillamail.databinding.ActivityMainBinding
import volovyk.guerrillamail.util.EmailValidator
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var assignedEmail: String? = null
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var adManager: AdManager

    @Inject
    lateinit var emailValidator: EmailValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adManager.initialize(applicationContext)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        val errorToast = Toast.makeText(this, R.string.common_failure, Toast.LENGTH_SHORT)

        binding.apply {
            emailTextView.setOnClickListener { copyEmailToClipboard() }
            emailDomainTextView.setOnClickListener { copyEmailToClipboard() }
            getNewAddressButton.setOnClickListener {
                getNewAddress(
                    "${emailUsernameEditText.text}${emailDomainTextView.text}"
                )
            }
            emailUsernameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) =
                    Unit

                override fun afterTextChanged(s: Editable) {
                    if (assignedEmail != null) {
                        getNewAddressButton.isVisible =
                            assignedEmail!!.emailUsernamePart() != s.toString()
                    }
                }
            })
        }

        lifecycleScope.launch {
            mainViewModel.uiState
                .map { it.assignedEmail }
                .distinctUntilChanged()
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect { email ->
                    if (email != null) {
                        binding.emailTextView.text = getString(R.string.your_temporary_email)
                        binding.emailUsernameEditText.setText(email.emailUsernamePart())
                        binding.emailDomainTextView.text = email.emailDomainPart()
                        assignedEmail = email
                        binding.getNewAddressButton.visibility = View.GONE
                    } else {
                        binding.emailTextView.text = getString(R.string.getting_temporary_email)
                        binding.emailUsernameEditText.setText("")
                    }
                }
        }

        lifecycleScope.launch {
            mainViewModel.uiState
                .map { it.state }
                .distinctUntilChanged()
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { state ->
                    binding.refreshingSpinner.isVisible = state is RemoteEmailDatabase.State.Loading
                    if (state is RemoteEmailDatabase.State.Failure) {
                        showFailureMessage(state.error, errorToast)
                    }
                }
        }
    }

    private fun showFailureMessage(error: Throwable, errorToast: Toast) {
        when (error) {
            is EmailAddressAssignmentException -> {
                errorToast.setText(
                    getString(
                        R.string.email_address_assignment_failure,
                        error.message
                    )
                )
                errorToast.show()
            }

            is EmailFetchException -> {
                errorToast.setText(getString(R.string.email_fetch_failure, error.message))
                errorToast.show()
            }

            else -> {
                errorToast.setText(R.string.common_failure)
                errorToast.show()
            }
        }
    }

    private fun getNewAddress(newAddress: String) {
        if (emailValidator.isValidEmailAddress(newAddress)) {
            val confirmationDialog = UiHelper.createConfirmationDialog(
                this,
                getString(R.string.confirm_getting_new_address, newAddress)
            ) {
                mainViewModel.setEmailAddress(newAddress.emailUsernamePart())
                binding.getNewAddressButton.visibility = View.GONE
            }

            confirmationDialog.show()
        } else {
            Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_SHORT).show()
        }
    }

    private fun String.emailUsernamePart(): String {
        return this.substringBefore("@")
    }

    private fun String.emailDomainPart(): String {
        return "@" + this.substringAfter("@")
    }

    private fun copyEmailToClipboard() {
        assignedEmail?.let { email ->
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.app_name), email)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, R.string.email_in_clipboard, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.d("onSupportNavigateUp")
        val navController = findNavController(R.id.my_nav_host_fragment)

        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}