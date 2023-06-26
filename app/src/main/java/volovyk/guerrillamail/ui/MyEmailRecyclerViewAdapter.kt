package volovyk.guerrillamail.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.databinding.FragmentEmailBinding

class MyEmailRecyclerViewAdapter(
    emails: LiveData<List<Email>>,
    lifecycleOwner: LifecycleOwner,
    private val onItemClick: (Int) -> Unit,
    private val onItemDeleteButtonClick: (Email) -> Unit
) : RecyclerView.Adapter<MyEmailRecyclerViewAdapter.ViewHolder>() {

    private var currentEmails: List<Email> = emptyList()

    init {
        emails.observe(lifecycleOwner) { newEmails ->
            val diffResult = DiffUtil.calculateDiff(EmailDiffCallback(currentEmails, newEmails))
            currentEmails = newEmails
            diffResult.dispatchUpdatesTo(this)
        }
    }

    private class EmailDiffCallback(
        private val oldList: List<Email>,
        private val newList: List<Email>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
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
        holder.mFromView.text = currentEmails[position].from
        holder.mSubjectView.text = currentEmails[position].subject
        holder.mEmailFragmentLayout.setOnClickListener {
            onItemClick(position)
        }
        holder.mDeleteButton.setOnClickListener {
            onItemDeleteButtonClick(currentEmails[position])
        }
    }

    override fun getItemCount(): Int {
        return currentEmails.size
    }

    class ViewHolder(binding: FragmentEmailBinding) : RecyclerView.ViewHolder(binding.root) {
        val mFromView: TextView
        val mSubjectView: TextView
        val mEmailFragmentLayout: ConstraintLayout
        val mDeleteButton: ImageButton

        init {
            mFromView = binding.from
            mSubjectView = binding.subject
            mEmailFragmentLayout = binding.emailFragmentLayout
            mDeleteButton = binding.deleteButton
        }
    }
}