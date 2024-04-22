package volovyk.guerrillamail.ui.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import volovyk.guerrillamail.ui.SingleEventEffect
import volovyk.guerrillamail.ui.handleSideEffect

@Composable
fun EmailDetails(
    emailId: String,
    modifier: Modifier = Modifier,
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
        modifier = modifier,
        uiState = uiState,
        onHtmlRenderSwitchCheckedChange = { viewModel.setHtmlRender(it) },
        onDisplayImagesSwitchCheckedChange = { viewModel.setDisplayImages(it) },
        onFromFieldClick = { viewModel.copySenderAddressToClipboard() }
    )
}