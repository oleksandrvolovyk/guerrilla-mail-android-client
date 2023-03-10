package volovyk.guerrillamail.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import volovyk.guerrillamail.R;
import volovyk.guerrillamail.data.model.Email;

@AndroidEntryPoint
public class SpecificEmailFragment extends Fragment {

    public static final String ARG_CHOSEN_EMAIL = "email";

    private int chosenEmail = 1;

    private MainViewModel mainViewModel;

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

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        TextView emailFromTextView = view.findViewById(R.id.fromTextView);
        TextView emailSubjectTextView = view.findViewById(R.id.subjectTextView);
        TextView emailDateTextView = view.findViewById(R.id.dateTextView);
        TextView emailBodyTextView = view.findViewById(R.id.bodyTextView);

        mainViewModel.getEmails().observe(getViewLifecycleOwner(), emails -> {
            Email chosenEmail = emails.get(this.chosenEmail);

            emailFromTextView.setText(getString(R.string.from, chosenEmail.getFrom()));
            emailSubjectTextView.setText(getString(R.string.subject, chosenEmail.getSubject()));
            emailDateTextView.setText(getString(R.string.date, chosenEmail.getDate()));
            emailBodyTextView.setText(getString(R.string.body, chosenEmail.getBody()));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_email, container, false);
    }
}