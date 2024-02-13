package volovyk.guerrillamail.ui.assigned

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import volovyk.guerrillamail.ui.SingleEventEffect
import volovyk.guerrillamail.ui.handleSideEffect

@Composable
fun AssignedEmail(
    modifier: Modifier = Modifier,
    viewModel: AssignedEmailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
        handleSideEffect(context, it)
    }

    AssignedEmailCard(
        modifier = modifier,
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