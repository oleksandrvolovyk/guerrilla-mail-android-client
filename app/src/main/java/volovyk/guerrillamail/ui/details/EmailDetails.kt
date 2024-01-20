package volovyk.guerrillamail.ui.details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import volovyk.guerrillamail.R
import volovyk.guerrillamail.ui.SideEffect
import volovyk.guerrillamail.ui.SingleEventEffect
import volovyk.guerrillamail.ui.UiHelper.showToast

@Composable
fun EmailDetails(
    emailId: String,
    viewModel: EmailDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(emailId) {
        viewModel.loadEmail(emailId)
    }

    SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
        handleSideEffect(context, it)
    }

    val uiState by viewModel.uiState.collectAsState()
    EmailDetailsScreen(
        uiState = uiState,
        onHtmlRenderSwitchCheckedChange = { viewModel.setHtmlRender(it) },
        onFromFieldClick = { viewModel.copySenderAddressToClipboard() }
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
    }
}