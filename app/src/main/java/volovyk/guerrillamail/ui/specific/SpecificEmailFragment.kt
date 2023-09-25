package volovyk.guerrillamail.ui.specific

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.ads.Ad
import volovyk.guerrillamail.data.ads.AdManager
import volovyk.guerrillamail.data.emails.model.Email
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

        val htmlRenderSwitchListener = { _: CompoundButton, isChecked: Boolean ->
            viewModel.setHtmlRender(isChecked)
        }
        binding.htmlRenderSwitch.setOnCheckedChangeListener(htmlRenderSwitchListener)

        binding.emailBodyWebView.apply {
            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        viewModel.uiState.observeWithViewLifecycle({ it.email }) { email ->
            email?.let {
                binding.apply {
                    fromTextView.text = getString(R.string.from, it.from)
                    subjectTextView.text = getString(R.string.subject, it.subject)
                    dateTextView.text = getString(R.string.date, it.date)
                    showEmailBody(it, viewModel.uiState.value.renderHtml)
                }
            }
        }

        viewModel.uiState.observeWithViewLifecycle({ it.renderHtml }) { renderHtml ->
            binding.apply {
                htmlRenderSwitch.setOnCheckedChangeListener(null)
                htmlRenderSwitch.isChecked = renderHtml
                htmlRenderSwitch.setOnCheckedChangeListener(htmlRenderSwitchListener)
                viewModel.uiState.value.email?.let {
                    showEmailBody(it, renderHtml)
                }
            }
        }
    }

    private fun showEmailBody(email: Email, renderHtml: Boolean) {
        binding.apply {
            if (renderHtml) {
                // Render html body
                emailBodyWebView.isVisible = true
                emailBodyScrollView?.isVisible = false
                emailBodyCardView.isVisible = false
                emailBodyWebView.loadData(email.htmlBody, "text/html", "base64")
            } else {
                // Show body as text
                emailBodyWebView.isVisible = false
                emailBodyScrollView?.isVisible = true
                emailBodyCardView.isVisible = true
                bodyTextView.text = email.body
            }
        }
    }
}