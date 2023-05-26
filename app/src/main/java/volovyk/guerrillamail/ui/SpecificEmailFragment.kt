package volovyk.guerrillamail.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.databinding.FragmentSpecificEmailBinding

@AndroidEntryPoint
class SpecificEmailFragment : Fragment() {
    private var chosenEmail = 1
    private var _binding: FragmentSpecificEmailBinding? = null
    private val binding get() = _binding!!

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
                binding.fromTextView.text = getString(R.string.from, chosenEmail.from)
                binding.subjectTextView.text = getString(R.string.subject, chosenEmail.subject)
                binding.dateTextView.text = getString(R.string.date, chosenEmail.date)
                binding.bodyTextView.text = Html.fromHtml(chosenEmail.body, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpecificEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_CHOSEN_EMAIL = "email"
    }
}