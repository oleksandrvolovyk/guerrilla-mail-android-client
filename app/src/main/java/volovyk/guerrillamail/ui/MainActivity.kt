package volovyk.guerrillamail.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.GuerrillaMailTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
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

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(uiState.isMainRemoteEmailDatabaseAvailable) {
                    if (uiState.isMainRemoteEmailDatabaseAvailable) {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    } else {
                        val result = snackbarHostState.showSnackbar(
                            message = getString(R.string.guerrilla_mail_offline),
                            actionLabel = getString(R.string.retry),
                            duration = SnackbarDuration.Indefinite
                        )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                viewModel.retryConnectingToMainDatabase()
                            }
                            SnackbarResult.Dismissed -> Unit
                        }
                    }
                }

                SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
                    handleSideEffect(this, it)
                }

                // TODO: Show loading indicator

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) { contentPadding ->
                    MainActivityContent(Modifier.padding(contentPadding))
                }
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
fun MainActivityContent(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Column(modifier = modifier) {
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