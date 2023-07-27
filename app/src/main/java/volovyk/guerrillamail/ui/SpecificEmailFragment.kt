package volovyk.guerrillamail.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R
import volovyk.guerrillamail.databinding.FragmentSpecificEmailBinding

@AndroidEntryPoint
class SpecificEmailFragment : Fragment() {
    private var chosenEmailId = 0
    private var _binding: FragmentSpecificEmailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(ARG_CHOSEN_EMAIL_ID)?.let { chosenEmailId = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel: MainViewModel by viewModels()
        mainViewModel.emails.observe(viewLifecycleOwner) { emails ->
            val chosenEmail = emails?.find { email -> email.id == chosenEmailId }
            chosenEmail?.let {
                binding.fromTextView.text = getString(R.string.from, it.from)
                binding.subjectTextView.text = getString(R.string.subject, it.subject)
                binding.dateTextView.text = getString(R.string.date, it.date)
                binding.bodyTextView.text = Html.fromHtml(it.body, Html.FROM_HTML_MODE_COMPACT)
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
        const val ARG_CHOSEN_EMAIL_ID = "email"
    }
}