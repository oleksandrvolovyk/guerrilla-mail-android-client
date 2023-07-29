package volovyk.guerrillamail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import volovyk.guerrillamail.BuildConfig
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.model.Email

/**
 * A fragment representing a list of Emails.
 */
@AndroidEntryPoint
class EmailFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()

    private var mInterstitialAd: InterstitialAd? = null

    private val emailListAdapter by lazy {
        EmailListAdapter(
            onItemClick = { position -> navigateToSpecificEmail(position) },
            onItemDeleteButtonClick = { email -> deleteEmail(email) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = emailListAdapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()

        val adId = if (BuildConfig.DEBUG) {
            BuildConfig.ADMOB_TEST_AD_ID
        } else {
            BuildConfig.ADMOB_MY_AD_ID
        }

        context?.let {
            InterstitialAd.load(
                it,
                adId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                    }
                })
        }

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                mInterstitialAd = null
            }
        }

        val navController = Navigation.findNavController(view)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.emailFragment) {
                // User has navigated back to the email list
                if (mInterstitialAd != null) {
                    activity?.let { mInterstitialAd?.show(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.uiState
                .map { it.emails }
                .distinctUntilChanged()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                .collect { emails ->
                    emailListAdapter.submitList(emails)
                }
        }
    }

    private fun navigateToSpecificEmail(email: Email) {
        val bundle = Bundle()
        bundle.putInt(SpecificEmailFragment.ARG_CHOSEN_EMAIL_ID, email.id)
        view?.let {
            Navigation.findNavController(it).navigate(
                R.id.action_emailFragment_to_specificEmailFragment2,
                bundle
            )
        }
    }

    private fun deleteEmail(email: Email) {
        val confirmationDialog = context?.let {
            UiHelper.createConfirmationDialog(
                it,
                it.getString(R.string.confirm_deleting_email)
            ) {
                mainViewModel.deleteEmail(email)
            }
        }
        confirmationDialog?.show()
    }
}