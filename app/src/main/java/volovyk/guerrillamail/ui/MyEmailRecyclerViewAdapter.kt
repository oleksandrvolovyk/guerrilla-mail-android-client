package volovyk.guerrillamail.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
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
        emails.observe(lifecycleOwner) {
            currentEmails = it
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