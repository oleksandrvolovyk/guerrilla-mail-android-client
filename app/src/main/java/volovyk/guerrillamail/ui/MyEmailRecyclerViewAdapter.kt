package volovyk.guerrillamail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.databinding.FragmentEmailBinding

class MyEmailRecyclerViewAdapter(viewModel: MainViewModel, lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<MyEmailRecyclerViewAdapter.ViewHolder>() {
    init {
        Companion.viewModel = viewModel
        viewModel.emails.observe(lifecycleOwner) { emails: List<Email?>? ->
            currentEmails = emails
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentEmailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mFromView.text = currentEmails!![position]?.from ?: "Unknown"
        holder.mSubjectView.text = currentEmails!![position]?.subject ?: "Unknown"
    }

    override fun getItemCount(): Int {
        return currentEmails!!.size
    }

    class ViewHolder(binding: FragmentEmailBinding) : RecyclerView.ViewHolder(binding.root) {
        val mFromView: TextView
        val mSubjectView: TextView

        init {
            mFromView = binding.from
            mSubjectView = binding.subject
            binding.emailFragmentLayout.setOnClickListener { openEmail(binding) }
            binding.deleteButton.setOnClickListener { deleteEmail() }
        }

        private fun openEmail(binding: FragmentEmailBinding) {
            val bundle = Bundle()
            bundle.putInt(SpecificEmailFragment.ARG_CHOSEN_EMAIL, layoutPosition)
            findNavController(binding.emailFragmentLayout).navigate(
                R.id.action_emailFragment_to_specificEmailFragment2,
                bundle
            )
        }

        private fun deleteEmail() {
            val context = itemView.context
            val confirmationDialog = UiHelper.createConfirmationDialog(
                context,
                context.getString(R.string.confirm_deleting_email)
            ) {
                viewModel!!.deleteEmail(currentEmails!![layoutPosition])
            }
            confirmationDialog.show()
        }

        override fun toString(): String {
            return super.toString() + " '" + mSubjectView.text + "'"
        }
    }

    companion object {
        private var currentEmails: List<Email?>? = ArrayList()
        private var viewModel: MainViewModel? = null
    }
}