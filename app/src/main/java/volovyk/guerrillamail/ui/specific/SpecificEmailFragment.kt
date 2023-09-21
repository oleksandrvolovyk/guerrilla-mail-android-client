package volovyk.guerrillamail.ui.specific

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.ads.Ad
import volovyk.guerrillamail.data.ads.AdManager
import volovyk.guerrillamail.databinding.FragmentSpecificEmailBinding
import volovyk.guerrillamail.ui.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class SpecificEmailFragment :
    BaseFragment<FragmentSpecificEmailBinding>(FragmentSpecificEmailBinding::inflate) {

    private val viewModel: SpecificEmailViewModel by viewModels()

    @Inject
    lateinit var adManager: AdManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adManager.loadAd(Ad.Interstitial)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.let { adManager.tryToShowAd(it, Ad.Interstitial) }
                this.remove()
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            owner = this,
            onBackPressedCallback = callback
        )
    }

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