package volovyk.guerrillamail.ui;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import volovyk.guerrillamail.R;
import volovyk.guerrillamail.data.model.Email;
import volovyk.guerrillamail.databinding.FragmentSpecificEmailBinding;

@AndroidEntryPoint
public class SpecificEmailFragment extends Fragment {

    public static final String ARG_CHOSEN_EMAIL = "email";
    FragmentSpecificEmailBinding binding;
    private int chosenEmail = 1;

    public SpecificEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            chosenEmail = getArguments().getInt(ARG_CHOSEN_EMAIL);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSpecificEmailBinding.inflate(getLayoutInflater());

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mainViewModel.getEmails().observe(getViewLifecycleOwner(), emails -> {
            Email chosenEmail = emails.get(this.chosenEmail);

            binding.fromTextView.setText(getString(R.string.from, chosenEmail.getFrom()));
            binding.subjectTextView.setText(getString(R.string.subject, chosenEmail.getSubject()));
            binding.dateTextView.setText(getString(R.string.date, chosenEmail.getDate()));
            binding.bodyTextView.setText(Html.fromHtml(chosenEmail.getBody(), Html.FROM_HTML_MODE_COMPACT));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_email, container, false);
    }
}