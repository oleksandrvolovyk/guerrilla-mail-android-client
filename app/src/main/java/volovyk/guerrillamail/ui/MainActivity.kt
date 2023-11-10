package volovyk.guerrillamail.ui

import android.os.Bundle
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
import volovyk.guerrillamail.data.emails.remote.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.data.emails.remote.exception.EmailFetchException
import volovyk.guerrillamail.databinding.ActivityMainBinding
import volovyk.guerrillamail.util.MessageHandler
import volovyk.guerrillamail.util.State
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var messageHandler: MessageHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        lifecycleScope.launch {
            mainViewModel.uiState
                .map { it.state }
                .distinctUntilChanged()
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { state ->
                    binding.refreshingSpinner.isVisible = state is State.Loading
                    if (state is State.Failure) {
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

    override fun onSupportNavigateUp(): Boolean {
        Timber.d("onSupportNavigateUp")
        val navController = findNavController(R.id.my_nav_host_fragment)

        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}