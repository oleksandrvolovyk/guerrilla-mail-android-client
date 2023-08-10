package volovyk.guerrillamail.ui.specific

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.databinding.FragmentSpecificEmailBinding
import volovyk.guerrillamail.ui.BaseFragment

@AndroidEntryPoint
class SpecificEmailFragment :
    BaseFragment<FragmentSpecificEmailBinding>(FragmentSpecificEmailBinding::inflate) {

    private val viewModel: SpecificEmailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observeWithViewLifecycle({ it.email }) { email ->
            email?.let {
                binding.fromTextView.text = getString(R.string.from, it.from)
                binding.subjectTextView.text = getString(R.string.subject, it.subject)
                binding.dateTextView.text = getString(R.string.date, it.date)
                binding.bodyTextView.text =
                    Html.fromHtml(it.body, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }
}