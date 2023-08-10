package volovyk.guerrillamail.ui

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.databinding.FragmentSpecificEmailBinding

@AndroidEntryPoint
class SpecificEmailFragment :
    BaseFragment<FragmentSpecificEmailBinding>(FragmentSpecificEmailBinding::inflate) {

    private var chosenEmailId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        arguments?.getInt(ARG_CHOSEN_EMAIL_ID)?.let { chosenEmailId = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel: MainViewModel by viewModels()

        mainViewModel.uiState.observeWithViewLifecycle({ it.emails }) { emails ->
            val chosenEmail = emails.find { email -> email.id == chosenEmailId }
            chosenEmail?.let {
                binding.fromTextView.text = getString(R.string.from, it.from)
                binding.subjectTextView.text = getString(R.string.subject, it.subject)
                binding.dateTextView.text = getString(R.string.date, it.date)
                binding.bodyTextView.text =
                    Html.fromHtml(it.body, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }

    companion object {
        const val ARG_CHOSEN_EMAIL_ID = "email"
    }
}