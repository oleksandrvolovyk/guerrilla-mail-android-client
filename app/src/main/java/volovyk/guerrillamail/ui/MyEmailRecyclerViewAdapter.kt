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

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            FragmentEmailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val email = currentEmails[position]
        holder.mFromView.text = email.from
        holder.mSubjectView.text = email.subject
        holder.mEmailFragmentLayout.setOnClickListener {
            onItemClick(position)
        }
        holder.mDeleteButton.setOnClickListener {
            onItemDeleteButtonClick(email)
        }
    }

    override fun getItemCount(): Int = currentEmails.size

    class ViewHolder(binding: FragmentEmailBinding) : RecyclerView.ViewHolder(binding.root) {
        val mFromView: TextView = binding.from
        val mSubjectView: TextView = binding.subject
        val mEmailFragmentLayout: ConstraintLayout = binding.emailFragmentLayout
        val mDeleteButton: ImageButton = binding.deleteButton
    }
}