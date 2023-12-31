package volovyk.guerrillamail.ui.assigned

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.GuerrillaMailTheme
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.ui.SideEffect
import volovyk.guerrillamail.ui.SingleEventEffect
import volovyk.guerrillamail.ui.UiHelper
import volovyk.guerrillamail.util.MessageHandler
import javax.inject.Inject

@AndroidEntryPoint
class AssignedEmailFragment : Fragment() {

    private val viewModel: AssignedEmailViewModel by viewModels()

    @Inject
    lateinit var messageHandler: MessageHandler

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
            }
        }
    }

    private fun handleSideEffect(
        it: SideEffect
    ) {
        when (it) {
            is SideEffect.CopyTextToClipboard -> {
                val clipboard =
                    requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                val clip =
                    ClipData.newPlainText(getString(R.string.app_name), it.text)
                clipboard.setPrimaryClip(clip)
            }

            is SideEffect.ShowToast -> {
                messageHandler.showMessage(getString(it.stringId))
            }

            is SideEffect.ConfirmAction -> {
                UiHelper.createConfirmationDialog(
                    requireContext(),
                    getString(it.messageStringId, it.stringFormatArg)
                ) {
                    it.action()
                }.show()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AssignedEmailCardPreview1() {
    AssignedEmailCard(
        emailUsername = "test",
        emailDomain = "guerrillamail.com",
        isGetNewAddressButtonVisible = false
    )
}

@Composable
@Preview(showBackground = true)
fun AssignedEmailCardPreview2() {
    AssignedEmailCard(
        emailUsername = "test2",
        emailDomain = "guerrillamail.com",
        isGetNewAddressButtonVisible = true
    )
}

@Composable
@Preview(showBackground = true)
fun AssignedEmailCardPreview3() {
    AssignedEmailCard(
        emailUsername = null,
        emailDomain = null,
        isGetNewAddressButtonVisible = false
    )
}