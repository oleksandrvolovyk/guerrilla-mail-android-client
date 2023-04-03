package volovyk.guerrillamail.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import volovyk.guerrillamail.R;
import volovyk.guerrillamail.data.model.Email;
import volovyk.guerrillamail.databinding.FragmentEmailBinding;

public class MyEmailRecyclerViewAdapter extends RecyclerView.Adapter<MyEmailRecyclerViewAdapter.ViewHolder> {

    private static List<Email> currentEmails = new ArrayList<>();
    private static MainViewModel viewModel;

    public MyEmailRecyclerViewAdapter(MainViewModel viewModel, LifecycleOwner lifecycleOwner) {
        MyEmailRecyclerViewAdapter.viewModel = viewModel;

        viewModel.getEmails().observe(lifecycleOwner, emails -> {
            currentEmails = emails;
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentEmailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mFromView.setText(currentEmails.get(position).getFrom());
        holder.mSubjectView.setText(currentEmails.get(position).getSubject());
    }

    @Override
    public int getItemCount() {
        return currentEmails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mFromView;
        public final TextView mSubjectView;

        public ViewHolder(FragmentEmailBinding binding) {
            super(binding.getRoot());
            mFromView = binding.from;
            mSubjectView = binding.subject;
            binding.emailFragmentLayout.setOnClickListener(v -> openEmail(binding));
            binding.deleteButton.setOnClickListener(v -> deleteEmail());
        }

        private void openEmail(FragmentEmailBinding binding) {
            Bundle bundle = new Bundle();
            bundle.putInt(SpecificEmailFragment.ARG_CHOSEN_EMAIL, getLayoutPosition());
            Navigation.findNavController(binding.emailFragmentLayout).navigate(R.id.action_emailFragment_to_specificEmailFragment2, bundle);
        }

        private void deleteEmail() {
            viewModel.deleteEmail(currentEmails.get(getLayoutPosition()));
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mSubjectView.getText() + "'";
        }
    }
}