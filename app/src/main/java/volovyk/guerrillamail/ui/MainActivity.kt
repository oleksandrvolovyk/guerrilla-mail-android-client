package volovyk.guerrillamail.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.SingleEvent
import volovyk.guerrillamail.databinding.ActivityMainBinding
import java.util.regex.Pattern

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var assignedEmail: String? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.assignedEmail?.observe(
            this
        ) { email: String? ->
            if (email != null) {
                binding.emailTextView.text = getString(R.string.your_temporary_email)
                binding.emailUsernameEditText.setText(email.substring(0, email.indexOf("@")))
                binding.emailDomainTextView.text = email.substring(email.indexOf("@"))
                assignedEmail = email
                binding.getNewAddressButton.visibility = View.GONE
            } else {
                binding.emailTextView.text = getString(R.string.getting_temporary_email)
                binding.emailUsernameEditText.setText("")
            }
        }
        binding.emailTextView.setOnClickListener { copyEmailToClipboard() }
        binding.emailDomainTextView.setOnClickListener { copyEmailToClipboard() }
        binding.getNewAddressButton.setOnClickListener {
            getNewAddress(
                binding.emailUsernameEditText.text.toString() +
                        binding.emailDomainTextView.text.toString()
            )
        }
        binding.emailUsernameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (assignedEmail != null) {
                    if (assignedEmail!!.substring(
                            0,
                            assignedEmail!!.indexOf("@")
                        ) != s.toString()
                    ) {
                        binding.getNewAddressButton.visibility = View.VISIBLE
                    } else {
                        binding.getNewAddressButton.visibility = View.GONE
                    }
                }
            }
        })
        mainViewModel.refreshing.observe(this) { refreshing: Boolean? ->
            if (refreshing!!) {
                binding.refreshingSpinner.visibility = View.VISIBLE
            } else {
                binding.refreshingSpinner.visibility = View.INVISIBLE
            }
        }
        mainViewModel.errorLiveData.observe(this) { errorEvent: SingleEvent<String> ->
            if (!errorEvent.hasBeenHandled()) {
                val errorText = errorEvent.contentIfNotHandled
                if (errorText != null) {
                    Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getNewAddress(newAddress: String) {
        if (isValidEmailAddress(newAddress)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.app_name)
            builder.setMessage(getString(R.string.confirm_getting_new_address, newAddress))
            builder.setIcon(R.drawable.ic_launcher_icon)
            builder.setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                mainViewModel.setEmailAddress(newAddress.substring(0, newAddress.indexOf("@")))
                binding.getNewAddressButton.visibility = View.GONE
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            val alert = builder.create()
            alert.show()
        } else {
            Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidEmailAddress(email: String): Boolean {
        val pattern = Pattern.compile("^.+@.+\\..+$")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun copyEmailToClipboard() {
        if (assignedEmail != null) {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.app_name), assignedEmail)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, R.string.email_in_clipboard, Toast.LENGTH_SHORT).show()
        }
    }
}