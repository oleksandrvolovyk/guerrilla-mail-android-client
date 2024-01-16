package volovyk.guerrillamail.ui.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EmailDetails(
    emailId: String,
    viewModel: EmailDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(emailId) {
        viewModel.loadEmail(emailId)
    }

    val uiState by viewModel.uiState.collectAsState()
    EmailDetailsScreen(
        uiState = uiState,
        onHtmlRenderSwitchCheckedChange = { viewModel.setHtmlRender(it) }
    )
}