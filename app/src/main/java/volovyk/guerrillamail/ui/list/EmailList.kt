package volovyk.guerrillamail.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import volovyk.guerrillamail.ui.SingleEventEffect
import volovyk.guerrillamail.ui.handleSideEffect

@Composable
fun EmailList(
    modifier: Modifier = Modifier,
    onNavigateToEmail: (emailId: String) -> Unit,
    viewModel: EmailListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
        handleSideEffect(context, it)
    }

    EmailListScreen(
        modifier = modifier,
        uiState = uiState,
        onItemClick = { email -> onNavigateToEmail(email.id) },
        onItemLongClick = { viewModel.toggleEmailSelection(it) },
        onClearSelectionButtonClick = { viewModel.clearSelectedEmails() },
        onSelectAllButtonClick = { viewModel.toggleSelectAllEmails() },
        onDeleteButtonClick = { viewModel.deleteSelectedEmails() }
    )
}