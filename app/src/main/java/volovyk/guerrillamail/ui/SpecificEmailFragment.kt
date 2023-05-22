package volovyk.guerrillamail.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.model.Email

@AndroidEntryPoint
class SpecificEmailFragment : Fragment() {
    private var chosenEmail = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            chosenEmail = requireArguments().getInt(ARG_CHOSEN_EMAIL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainViewModel.emails?.observe(viewLifecycleOwner) { emails: List<Email?>? ->
            val chosenEmail = emails?.get(chosenEmail)
            if (chosenEmail != null) {
                (view.findViewById<View>(R.id.fromTextView) as TextView).text =
                    getString(R.string.from, chosenEmail.from)
                (view.findViewById<View>(R.id.subjectTextView) as TextView).text =
                    getString(R.string.subject, chosenEmail.subject)
                (view.findViewById<View>(R.id.dateTextView) as TextView).text =
                    getString(R.string.date, chosenEmail.date)
                (view.findViewById<View>(R.id.bodyTextView) as TextView).text =
                    Html.fromHtml(chosenEmail.body, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_email, container, false)
    }

    companion object {
        const val ARG_CHOSEN_EMAIL = "email"
    }
}