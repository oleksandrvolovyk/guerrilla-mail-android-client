package volovyk.guerrillamail.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.databinding.FragmentEmailBinding

class EmailListAdapter(
    private val onItemClick: (Email) -> Unit,
    private val onItemDeleteButtonClick: (Email) -> Unit
) : ListAdapter<Email, EmailListAdapter.EmailViewHolder>(EmailDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmailViewHolder {
        return EmailViewHolder(
            FragmentEmailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClick, onItemDeleteButtonClick
        )
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) =
        holder.bind(getItem(position))

    class EmailViewHolder(
        private val binding: FragmentEmailBinding,
        private val onItemClick: (Email) -> Unit,
        private val onItemDeleteButtonClick: (Email) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(email: Email) {
            binding.from.text = email.from
            binding.subject.text = email.subject
            binding.emailFragmentLayout.setOnClickListener {
                onItemClick(email)
            }
            binding.deleteButton.setOnClickListener {
                onItemDeleteButtonClick(email)
            }
        }
    }

    // DiffUtil callback
    private class EmailDiffCallBack : DiffUtil.ItemCallback<Email>() {
        override fun areItemsTheSame(oldItem: Email, newItem: Email): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Email, newItem: Email): Boolean =
            oldItem == newItem
    }
}