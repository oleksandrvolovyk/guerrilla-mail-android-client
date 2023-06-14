package volovyk.guerrillamail.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.databinding.ActivityMainBinding
import java.util.regex.Pattern

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var assignedEmail: String? = null
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            emailTextView.setOnClickListener { copyEmailToClipboard() }
            emailDomainTextView.setOnClickListener { copyEmailToClipboard() }
            getNewAddressButton.setOnClickListener {
                getNewAddress(
                    "${emailUsernameEditText.text}${emailDomainTextView.text}"
                )
            }
            emailUsernameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (assignedEmail != null) {
                        getNewAddressButton.visibility =
                            if (assignedEmail!!.emailUsernamePart() != s.toString()) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }
                    }
                }
            })
        }

        mainViewModel.assignedEmail.observe(this) { email ->
            if (email != null) {
                binding.emailTextView.text = getString(R.string.your_temporary_email)
                binding.emailUsernameEditText.setText(email.emailUsernamePart())
                binding.emailDomainTextView.text = email.emailDomainPart()
                assignedEmail = email
                binding.getNewAddressButton.visibility = View.GONE
            } else {
                binding.emailTextView.text = getString(R.string.getting_temporary_email)
                binding.emailUsernameEditText.setText("")
            }
        }

        mainViewModel.refreshing.observe(this) { refreshing ->
            binding.refreshingSpinner.visibility = if (refreshing == true) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

        mainViewModel.errorLiveData.observe(this) { errorEvent ->
            errorEvent.contentIfNotHandled.let { errorText ->
                Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getNewAddress(newAddress: String) {
        if (newAddress.isValidEmailAddress()) {
            val confirmationDialog = UiHelper.createConfirmationDialog(
                this, getString(R.string.confirm_getting_new_address, newAddress)
            ) {
                mainViewModel.setEmailAddress(newAddress.emailUsernamePart())
                binding.getNewAddressButton.visibility = View.GONE
            }

            confirmationDialog.show()
        } else {
            Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_SHORT).show()
        }
    }

    private fun String.isValidEmailAddress(): Boolean {
        val pattern = Pattern.compile("^.+@.+\\..+$")
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

    private fun String.emailUsernamePart(): String {
        return this.substringBefore("@")
    }

    private fun String.emailDomainPart(): String {
        return "@" + this.substringAfter("@")
    }

    private fun copyEmailToClipboard() {
        assignedEmail?.let { email ->
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.app_name), email)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, R.string.email_in_clipboard, Toast.LENGTH_SHORT).show()
        }
    }
}