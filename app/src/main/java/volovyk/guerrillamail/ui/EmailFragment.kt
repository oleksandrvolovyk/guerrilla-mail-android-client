package volovyk.guerrillamail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.R

/**
 * A fragment representing a list of Emails.
 */
@AndroidEntryPoint
class EmailFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_list, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = MyEmailRecyclerViewAdapter(mainViewModel, viewLifecycleOwner)
        }
        return view
    }
}