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
import volovyk.guerrillamail.ui.UiHelper
import volovyk.guerrillamail.util.EmailValidator
import volovyk.guerrillamail.util.MessageHandler
import javax.inject.Inject

@AndroidEntryPoint
class AssignedEmailFragment : Fragment() {

    private val viewModel: AssignedEmailViewModel by viewModels()

    @Inject
    lateinit var emailValidator: EmailValidator

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

                    AssignedEmailCard(
                        emailUsername = uiState.emailUsername,
                        emailDomain = uiState.emailDomain,
                        isGetNewAddressButtonVisible = uiState.isGetNewAddressButtonVisible,
                        onEmailAddressClick = {
                            uiState.emailUsername?.let {
                                copyEmailToClipboard(uiState.emailUsername + "@" + uiState.emailDomain)
                                messageHandler.showMessage(context.getString(R.string.email_in_clipboard))
                            }
                        },
                        onGetNewAddressButtonClick = {
                            uiState.emailUsername?.let {
                                getNewAddress("${uiState.emailUsername}@${uiState.emailDomain}")
                            }
                        },
                        onEmailUsernameValueChange = { viewModel.userChangedEmailUsername(it) }
                    )
                }
            }
        }
    }

    private fun copyEmailToClipboard(email: String) {
        val clipboard =
            requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.app_name), email)
        clipboard.setPrimaryClip(clip)
    }

    private fun getNewAddress(newAddress: String) {
        return if (emailValidator.isValidEmailAddress(newAddress)) {
            val confirmationDialog = UiHelper.createConfirmationDialog(
                requireContext(),
                getString(R.string.confirm_getting_new_address, newAddress)
            ) {
                viewModel.setEmailAddress(newAddress)
            }

            confirmationDialog.show()
        } else {
            messageHandler.showMessage(getString(R.string.email_invalid))
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