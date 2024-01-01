package volovyk.guerrillamail.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.compose.GuerrillaMailTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.SideEffect
import volovyk.guerrillamail.ui.SingleEventEffect
import volovyk.guerrillamail.ui.UiHelper

/**
 * A fragment representing a list of Emails.
 */
@AndroidEntryPoint
class EmailListFragment : Fragment() {

    private val viewModel: EmailListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GuerrillaMailTheme {
                    val uiState by viewModel.uiState.collectAsState()

                    SingleEventEffect(sideEffectFlow = viewModel.sideEffectFlow) {
                        handleSideEffect(it)
                    }

                    EmailList(
                        emails = uiState.emails,
                        onItemClick = ::navigateToSpecificEmail,
                        onItemDeleteButtonClick = { viewModel.deleteEmail(it) },
                        onItemDeleteButtonLongClick = { viewModel.deleteAllEmails() }
                    )
                }
            }
        }
    }

    private fun navigateToSpecificEmail(email: Email) {
        Timber.d("Opening email: ${email.subject}")
        val action = EmailListFragmentDirections.actionOpenEmail(email.id, email.subject)
        findNavController().navigate(action)
    }

    private fun handleSideEffect(sideEffect: SideEffect) {
        when (sideEffect) {
            is SideEffect.ConfirmAction -> {
                UiHelper.createConfirmationDialog(
                    requireContext(),
                    getString(sideEffect.messageStringId, sideEffect.stringFormatArg)
                ) {
                    sideEffect.action()
                }.show()
            }
        }
    }
}