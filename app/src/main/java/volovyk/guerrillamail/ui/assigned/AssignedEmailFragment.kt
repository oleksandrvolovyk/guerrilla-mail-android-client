package volovyk.guerrillamail.ui.assigned

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.GuerrillaMailTheme
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.remote.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.ui.UiHelper
import volovyk.guerrillamail.util.EmailValidator
import volovyk.guerrillamail.util.MessageHandler
import volovyk.guerrillamail.util.State
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
                    var emailUsername by remember { mutableStateOf<String?>("") }
                    var isGetNewAddressButtonVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(uiState.assignedEmail) {
                        emailUsername = uiState.assignedEmail?.emailUsernamePart()
                    }

                    LaunchedEffect(emailUsername) {
                        uiState.assignedEmail?.emailUsernamePart().let {
                            isGetNewAddressButtonVisible = emailUsername != it
                        }
                    }

                    LaunchedEffect(uiState.state) {
                        if (uiState.state is State.Failure &&
                            (uiState.state as State.Failure).error is EmailAddressAssignmentException
                        ) {
                            emailUsername = uiState.assignedEmail?.emailUsernamePart()
                        }
                    }

                    AssignedEmailCard(
                        emailUsername = emailUsername,
                        emailDomain = uiState.assignedEmail?.emailDomainPart(),
                        isGetNewAddressButtonVisible = isGetNewAddressButtonVisible,
                        onEmailAddressClick = {
                            uiState.assignedEmail?.let {
                                copyEmailToClipboard(it)
                                messageHandler.showMessage(context.getString(R.string.email_in_clipboard))
                            }
                        },
                        onGetNewAddressButtonClick = {
                            uiState.assignedEmail?.let {
                                getNewAddress("$emailUsername@${it.emailDomainPart()}")
                                isGetNewAddressButtonVisible = false
                            }
                        },
                        onEmailUsernameValueChange = { newEmailUsername ->
                            emailUsername = newEmailUsername
                        }
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

    private fun String.emailUsernamePart(): String {
        return this.substringBefore("@")
    }

    private fun String.emailDomainPart(): String {
        return this.substringAfter("@")
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