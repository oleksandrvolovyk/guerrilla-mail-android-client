package volovyk.guerrillamail.ui.assigned

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.remote.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.databinding.FragmentAssignedEmailBinding
import volovyk.guerrillamail.ui.BaseFragment
import volovyk.guerrillamail.ui.UiHelper
import volovyk.guerrillamail.util.EmailValidator
import volovyk.guerrillamail.util.MessageHandler
import volovyk.guerrillamail.util.State
import javax.inject.Inject

@AndroidEntryPoint
class AssignedEmailFragment :
    BaseFragment<FragmentAssignedEmailBinding>(FragmentAssignedEmailBinding::inflate) {

    private val viewModel: AssignedEmailViewModel by viewModels()

    @Inject
    lateinit var emailValidator: EmailValidator

    @Inject
    lateinit var messageHandler: MessageHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            emailTextView.setOnClickListener { copyEmailToClipboard() }
            emailDomainTextView.setOnClickListener { copyEmailToClipboard() }
            getNewAddressButton.setOnClickListener {
                getNewAddress("${emailUsernameEditText.text}${emailDomainTextView.text}")
            }
            emailUsernameEditText.addTextChangedListener(
                object : UiHelper.SimpleTextWatcher() {
                    override fun afterTextChanged(s: Editable) {
                        viewModel.uiState.value.assignedEmail?.let {
                            getNewAddressButton.isVisible = it.emailUsernamePart() != s.toString()
                        }
                    }
                }
            )
        }

        viewModel.uiState.observeWithViewLifecycle({ it.assignedEmail }) { email ->
            if (email != null) {
                binding.emailLinearLayout.isVisible = true
                binding.emailTextView.text = getString(R.string.your_temporary_email)
                binding.emailUsernameEditText.setText(email.emailUsernamePart())
                binding.emailDomainTextView.text = email.emailDomainPart()
                binding.getNewAddressButton.visibility = View.GONE
            } else {
                binding.emailLinearLayout.isVisible = false
                binding.emailTextView.text = getString(R.string.getting_temporary_email)
                binding.emailUsernameEditText.setText("")
            }
        }

        viewModel.uiState.observeWithViewLifecycle({ it.state }) { state ->
            if (state is State.Failure && state.error is EmailAddressAssignmentException) {
                viewModel.uiState.value.assignedEmail?.let {
                    binding.emailUsernameEditText.setText(it.emailUsernamePart())
                    binding.emailDomainTextView.text = it.emailDomainPart()
                }
            }
        }
    }

    private fun getNewAddress(newAddress: String) {
        if (emailValidator.isValidEmailAddress(newAddress)) {
            val confirmationDialog = UiHelper.createConfirmationDialog(
                requireContext(),
                getString(R.string.confirm_getting_new_address, newAddress)
            ) {
                viewModel.setEmailAddress(newAddress)
                binding.getNewAddressButton.visibility = View.GONE
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
        return "@" + this.substringAfter("@")
    }

    private fun copyEmailToClipboard() {
        viewModel.uiState.value.assignedEmail?.let { email ->
            val clipboard =
                requireContext().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.app_name), email)
            clipboard.setPrimaryClip(clip)
            messageHandler.showMessage(getString(R.string.email_in_clipboard))
        }
    }
}