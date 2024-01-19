package volovyk.guerrillamail.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
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

                SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
                    handleSideEffect(this, it)
                }

                MainActivityContent(
                    uiState = uiState,
                    onRetryConnectingToMainDatabase = { viewModel.retryConnectingToMainDatabase() }
                )
            }
        }
    }
}

private fun handleSideEffect(context: Context, sideEffect: SideEffect) {
    when (sideEffect) {
        is SideEffect.ShowToast -> {
            context.showToast(context.getString(sideEffect.stringId, sideEffect.stringFormatArg))
        }
    }
}

@Composable
fun MainActivityContent(uiState: UiState, onRetryConnectingToMainDatabase: () -> Unit = {}) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isMainRemoteEmailDatabaseAvailable) {
        if (uiState.isMainRemoteEmailDatabaseAvailable) {
            snackbarHostState.currentSnackbarData?.dismiss()
        } else {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.guerrilla_mail_offline),
                actionLabel = context.getString(R.string.retry),
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> onRetryConnectingToMainDatabase()
                SnackbarResult.Dismissed -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(stringResource(R.string.test_tag_loading_indicator))
                )
            }

            AssignedEmail()

            NavHost(navController = navController, startDestination = "emails") {
                composable("emails") {
                    EmailList(
                        onNavigateToEmail = { emailId ->
                            navController.navigate("emails/${emailId}")
                        }
                    )
                }
                composable(
                    route = "emails/{emailId}",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    }
                ) { navBackStackEntry ->
                    navBackStackEntry.arguments?.getString("emailId")
                        ?.let { EmailDetails(emailId = it) }
                }
            }
        }
    }
}