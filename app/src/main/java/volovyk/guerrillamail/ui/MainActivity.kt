package volovyk.guerrillamail.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.compose.GuerrillaMailTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.ui.assigned.AssignedEmail
import volovyk.guerrillamail.ui.details.EmailDetails
import volovyk.guerrillamail.ui.list.EmailList

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            GuerrillaMailTheme {
                MainActivityContent()
            }
        }

//        val binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//
//        setupActionBarWithNavController(navController)

        // TODO: Redo with side-effects
//        lifecycleScope.launch {
//            mainViewModel.uiState
//                .map { it.state }
//                .distinctUntilChanged()
//                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                .collect { state ->
//                    binding.refreshingSpinner.isVisible = state is State.Loading
//                    if (state is State.Failure) {
//                        showFailureMessage(state.error)
//                    }
//                }
//        }

//        val guerrillaMailOfflineSnackbar = Snackbar.make(
//            binding.root,
//            getString(R.string.guerrilla_mail_offline),
//            Snackbar.LENGTH_INDEFINITE
//        ).setAction(R.string.retry) {
//            mainViewModel.retryConnectingToMainDatabase()
//        }
//
//        lifecycleScope.launch {
//            mainViewModel.uiState
//                .map { it.mainRemoteEmailDatabaseIsAvailable }
//                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                .collect { mainRemoteEmailDatabaseIsAvailable ->
//                    if (mainRemoteEmailDatabaseIsAvailable) {
//                        guerrillaMailOfflineSnackbar.dismiss()
//                    } else {
//                        guerrillaMailOfflineSnackbar.show()
//                    }
//                }
//        }
    }

//    private fun showFailureMessage(error: Throwable) {
//        when (error) {
//            is EmailAddressAssignmentException -> {
//                messageHandler.showMessage(
//                    getString(
//                        R.string.email_address_assignment_failure,
//                        error.message
//                    )
//                )
//            }
//
//            is EmailFetchException -> messageHandler.showMessage(
//                getString(
//                    R.string.email_fetch_failure,
//                    error.message
//                )
//            )
//
//            else -> messageHandler.showMessage(getString(R.string.common_failure))
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.d("onSupportNavigateUp")
        val navController = findNavController(R.id.my_nav_host_fragment)

        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

@Composable
fun MainActivityContent() {
    val navController = rememberNavController()

    Column {
        AssignedEmail()
        NavHost(navController = navController, startDestination = "emails") {
            composable("emails") {
                EmailList(
                    onNavigateToEmail = { emailId ->
                        navController.navigate("emails/${emailId}")
                    }
                )
            }
            composable("emails/{emailId}") { navBackStackEntry ->
                navBackStackEntry.arguments?.getString("emailId")
                    ?.let { EmailDetails(emailId = it) }
            }
        }
    }
}