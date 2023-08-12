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
    private val onItemDeleteButtonClick: (Email) -> Unit,
    private val onItemDeleteButtonLongClick: (Email) -> Unit
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
            ), onItemClick, onItemDeleteButtonClick, onItemDeleteButtonLongClick
        )
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) =
        holder.bind(getItem(position))

    class EmailViewHolder(
        private val binding: FragmentEmailBinding,
        private val onItemClick: (Email) -> Unit,
        private val onItemDeleteButtonClick: (Email) -> Unit,
        private val onItemDeleteButtonLongClick: (Email) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(email: Email) {
            binding.apply {
                from.text = email.from
                subject.text = email.subject
                emailFragmentLayout.setOnClickListener {
                    onItemClick(email)
                }
                deleteButton.apply {
                    setOnClickListener {
                        onItemDeleteButtonClick(email)
                    }
                    setOnLongClickListener {
                        onItemDeleteButtonLongClick(email)
                        true
                    }
                }
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