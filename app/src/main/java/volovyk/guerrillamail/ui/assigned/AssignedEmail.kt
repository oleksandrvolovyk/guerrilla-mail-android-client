package volovyk.guerrillamail.ui.assigned

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import volovyk.guerrillamail.R
import volovyk.guerrillamail.ui.SideEffect
import volovyk.guerrillamail.ui.SingleEventEffect
import volovyk.guerrillamail.ui.UiHelper.createConfirmationDialog
import volovyk.guerrillamail.ui.UiHelper.showToast

@Composable
fun AssignedEmail(
    viewModel: AssignedEmailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
        handleSideEffect(context, it)
    }

    AssignedEmailCard(
        emailUsername = uiState.emailUsername,
        emailDomain = uiState.emailDomain,
        isGetNewAddressButtonVisible = uiState.isGetNewAddressButtonVisible,
        onEmailAddressClick = {
            viewModel.copyEmailAddressToClipboard()
        },
        onGetNewAddressButtonClick = {
            uiState.emailUsername?.let {
                viewModel.setEmailAddress("${uiState.emailUsername}@${uiState.emailDomain}")
            }
        },
        onEmailUsernameValueChange = { viewModel.userChangedEmailUsername(it) }
    )
}

private fun handleSideEffect(
    context: Context,
    sideEffect: SideEffect
) {
    when (sideEffect) {
        is SideEffect.CopyTextToClipboard -> {
            val clipboard =
                context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip =
                ClipData.newPlainText(context.getString(R.string.app_name), sideEffect.text)
            clipboard.setPrimaryClip(clip)
        }

        is SideEffect.ShowToast -> {
            context.showToast(context.getString(sideEffect.stringId))
        }

        is SideEffect.ConfirmAction -> {
            createConfirmationDialog(
                context,
                context.getString(sideEffect.messageStringId, sideEffect.stringFormatArg)
            ) {
                sideEffect.action()
            }.show()
        }
    }
}
