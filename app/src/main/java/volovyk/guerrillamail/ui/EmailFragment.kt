package volovyk.guerrillamail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.model.Email

/**
 * A fragment representing a list of Emails.
 */
@AndroidEntryPoint
class EmailFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = MyEmailRecyclerViewAdapter(
                mainViewModel.emails,
                viewLifecycleOwner,
                onItemClick = { position -> navigateToSpecificEmail(position) },
                onItemDeleteButtonClick = { email -> deleteEmail(email) })
        }
        return view
    }

    private fun navigateToSpecificEmail(position: Int) {
        val bundle = Bundle()
        bundle.putInt(SpecificEmailFragment.ARG_CHOSEN_EMAIL, position)
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