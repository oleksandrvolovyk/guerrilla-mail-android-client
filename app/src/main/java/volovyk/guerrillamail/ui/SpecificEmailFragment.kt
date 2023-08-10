package volovyk.guerrillamail.ui

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.databinding.FragmentSpecificEmailBinding

@AndroidEntryPoint
class SpecificEmailFragment :
    BaseFragment<FragmentSpecificEmailBinding>(FragmentSpecificEmailBinding::inflate) {

    private val args: SpecificEmailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel: MainViewModel by viewModels()

        mainViewModel.uiState.observeWithViewLifecycle({ it.emails }) { emails ->
            val chosenEmail = emails.find { email -> email.id == args.emailId }
            chosenEmail?.let {
                binding.fromTextView.text = getString(R.string.from, it.from)
                binding.subjectTextView.text = getString(R.string.subject, it.subject)
                binding.dateTextView.text = getString(R.string.date, it.date)
                binding.bodyTextView.text =
                    Html.fromHtml(it.body, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }
}