package volovyk.guerrillamail.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.ads.Ad
import volovyk.guerrillamail.data.ads.AdManager
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.databinding.FragmentEmailListBinding
import volovyk.guerrillamail.ui.BaseFragment
import volovyk.guerrillamail.ui.UiHelper
import javax.inject.Inject

/**
 * A fragment representing a list of Emails.
 */
@AndroidEntryPoint
class EmailListFragment :
    BaseFragment<FragmentEmailListBinding>(FragmentEmailListBinding::inflate) {

    private val viewModel: EmailListViewModel by viewModels()

    @Inject
    lateinit var adManager: AdManager

    private val emailListAdapter by lazy {
        EmailListAdapter(
            onItemClick = { email -> navigateToSpecificEmail(email) },
            onItemDeleteButtonClick = { email -> deleteEmail(email) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.emailListFragment) {
                // User has navigated back to the email list
                activity?.let { adManager.tryToShowAd(it, Ad.Interstitial) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = emailListAdapter
        }

        context?.let { adManager.loadAd(it, Ad.Interstitial) }

        viewModel.uiState.observeWithViewLifecycle({ it.emails }) { emails ->
            emailListAdapter.submitList(emails)
        }
    }

    private fun navigateToSpecificEmail(email: Email) {
        Timber.d("Opening email: ${email.subject}")
        val action = EmailListFragmentDirections.actionOpenEmail(email.id, email.subject)
        findNavController().navigate(action)
    }

    private fun deleteEmail(email: Email) {
        val confirmationDialog = context?.let {
            UiHelper.createConfirmationDialog(
                it,
                it.getString(R.string.confirm_deleting_email)
            ) {
                viewModel.deleteEmail(email)
            }
        }
        confirmationDialog?.show()
    }
}