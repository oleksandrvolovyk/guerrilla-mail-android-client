package volovyk.guerrillamail.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.GuerrillaMailTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.ui.UiHelper.showToast
import volovyk.guerrillamail.ui.assigned.AssignedEmail
import volovyk.guerrillamail.ui.details.EmailDetails
import volovyk.guerrillamail.ui.list.EmailList

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            GuerrillaMailTheme {
                val uiState by viewModel.uiState.collectAsState()

                SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
                    handleSideEffect(this, it)
                }

                // TODO: Show loading indicator
                // TODO: Show "Guerrilla Mail is not available" snackbar

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

//    override fun onSupportNavigateUp(): Boolean {
//        Timber.d("onSupportNavigateUp")
//        val navController = findNavController(R.id.my_nav_host_fragment)
//
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
}

private fun handleSideEffect(context: Context, sideEffect: SideEffect) {
    when (sideEffect) {
        is SideEffect.ShowToast -> {
            context.showToast(context.getString(sideEffect.stringId, sideEffect.stringFormatArg))
        }
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