package volovyk.guerrillamail.ui.list

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import volovyk.guerrillamail.ui.SideEffect
import volovyk.guerrillamail.ui.SingleEventEffect
import volovyk.guerrillamail.ui.UiHelper

@Composable
fun EmailList(
    onNavigateToEmail: (emailId: String) -> Unit,
    viewModel: EmailListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
        handleSideEffect(context, it)
    }

    EmailListScreen(
        emails = uiState.emails,
        onItemClick = { email -> onNavigateToEmail(email.id) },
        onItemDeleteButtonClick = { viewModel.deleteEmail(it) },
        onItemDeleteButtonLongClick = { viewModel.deleteAllEmails() }
    )
}

private fun handleSideEffect(context: Context, sideEffect: SideEffect) {
    when (sideEffect) {
        is SideEffect.ConfirmAction -> {
            UiHelper.createConfirmationDialog(
                context,
                context.getString(sideEffect.messageStringId, sideEffect.stringFormatArg)
            ) {
                sideEffect.action()
            }.show()
        }
    }
}