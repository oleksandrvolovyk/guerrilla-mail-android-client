package volovyk.guerrillamail.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.ads.AdManager
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.emails.remote.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.data.emails.remote.exception.EmailFetchException
import volovyk.guerrillamail.databinding.ActivityMainBinding
import volovyk.guerrillamail.util.EmailValidator
import volovyk.guerrillamail.util.MessageHandler
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

    @Inject
    lateinit var messageHandler: MessageHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        binding.apply {
            emailTextView.setOnClickListener { copyEmailToClipboard() }
            emailDomainTextView.setOnClickListener { copyEmailToClipboard() }
            getNewAddressButton.setOnClickListener {
                getNewAddress(
                    "${emailUsernameEditText.text}${emailDomainTextView.text}"
                )
            }
            emailUsernameEditText.addTextChangedListener(
                UiHelper.AfterTextChangedWatcher { editable ->
                    if (assignedEmail != null) {
                        getNewAddressButton.isVisible =
                            assignedEmail!!.emailUsernamePart() != editable.toString()
                    }
                }
            )
        }

        lifecycleScope.launch {
            mainViewModel.uiState
                .map { it.assignedEmail }
                .distinctUntilChanged()
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect { email ->
                    if (email != null) {
                        binding.emailLinearLayout.isVisible = true
                        binding.emailTextView.text = getString(R.string.your_temporary_email)
                        binding.emailUsernameEditText.setText(email.emailUsernamePart())
                        binding.emailDomainTextView.text = email.emailDomainPart()
                        assignedEmail = email
                        binding.getNewAddressButton.visibility = View.GONE
                    } else {
                        binding.emailLinearLayout.isVisible = false
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
                        showFailureMessage(state.error)
                    }
                }
        }

        val guerrillaMailOfflineSnackbar = Snackbar.make(
            binding.root,
            getString(R.string.guerrilla_mail_offline),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.retry) {
            mainViewModel.retryConnectingToMainDatabase()
        }
        lifecycleScope.launch {
            mainViewModel.uiState
                .map { it.mainRemoteEmailDatabaseIsAvailable }
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { mainRemoteEmailDatabaseIsAvailable ->
                    if (mainRemoteEmailDatabaseIsAvailable) {
                        guerrillaMailOfflineSnackbar.dismiss()
                    } else {
                        guerrillaMailOfflineSnackbar.show()
                    }
                }
        }
    }

    private fun showFailureMessage(error: Throwable) {
        when (error) {
            is EmailAddressAssignmentException -> {
                messageHandler.showMessage(
                    getString(
                        R.string.email_address_assignment_failure,
                        error.message
                    )
                )
                mainViewModel.uiState.value.assignedEmail?.let {
                    binding.emailUsernameEditText.setText(it.emailUsernamePart())
                    binding.emailDomainTextView.text = it.emailDomainPart()
                }
            }

            is EmailFetchException -> messageHandler.showMessage(
                getString(
                    R.string.email_fetch_failure,
                    error.message
                )
            )

            else -> messageHandler.showMessage(getString(R.string.common_failure))
        }
    }

    private fun getNewAddress(newAddress: String) {
        if (emailValidator.isValidEmailAddress(newAddress)) {
            val confirmationDialog = UiHelper.createConfirmationDialog(
                this,
                getString(R.string.confirm_getting_new_address, newAddress)
            ) {
                mainViewModel.setEmailAddress(newAddress)
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