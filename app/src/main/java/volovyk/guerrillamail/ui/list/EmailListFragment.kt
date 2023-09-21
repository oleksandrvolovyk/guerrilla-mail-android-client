package volovyk.guerrillamail.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.databinding.FragmentEmailListBinding
import volovyk.guerrillamail.ui.BaseFragment
import volovyk.guerrillamail.ui.UiHelper

/**
 * A fragment representing a list of Emails.
 */
@AndroidEntryPoint
class EmailListFragment :
    BaseFragment<FragmentEmailListBinding>(FragmentEmailListBinding::inflate) {

    private val viewModel: EmailListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        val emailListAdapter = EmailListAdapter(
            onItemClick = ::navigateToSpecificEmail,
            onItemDeleteButtonClick = ::deleteEmail,
            onItemDeleteButtonLongClick = { deleteAllEmails() }
        )

        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = emailListAdapter
        }

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
        context?.let {
            val confirmationDialog = UiHelper.createConfirmationDialog(
                it,
                it.getString(R.string.confirm_deleting_email)
            ) {
                viewModel.deleteEmail(email)
            }
            confirmationDialog.show()
        }
    }

    private fun deleteAllEmails() {
        context?.let {
            val confirmationDialog = UiHelper.createConfirmationDialog(
                it,
                it.getString(R.string.confirm_deleting_all_emails)
            ) {
                viewModel.deleteAllEmails()
            }
            confirmationDialog.show()
        }
    }
}