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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.ads.Ad
import volovyk.guerrillamail.data.ads.AdManager
import volovyk.guerrillamail.data.model.Email
import javax.inject.Inject

/**
 * A fragment representing a list of Emails.
 */
@AndroidEntryPoint
class EmailFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var adManager: AdManager

    private val emailListAdapter by lazy {
        EmailListAdapter(
            onItemClick = { position -> navigateToSpecificEmail(position) },
            onItemDeleteButtonClick = { email -> deleteEmail(email) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.emailFragment) {
                // User has navigated back to the email list
                activity?.let { adManager.tryToShowAd(it, Ad.Interstitial) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
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
        Timber.d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        context?.let { adManager.loadAd(it, Ad.Interstitial) }

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
            Timber.d("Opening email ${email.id}")
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