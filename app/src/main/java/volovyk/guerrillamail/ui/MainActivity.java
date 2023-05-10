package volovyk.guerrillamail.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dagger.hilt.android.AndroidEntryPoint;
import volovyk.guerrillamail.R;
import volovyk.guerrillamail.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private String assignedEmail;
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getAssignedEmail().observe(this,
                email -> {
                    if (email != null) {
                        binding.emailTextView.setText(getString(R.string.your_temporary_email));
                        binding.emailUsernameEditText.setText(email.substring(0, email.indexOf("@")));
                        binding.emailDomainTextView.setText(email.substring(email.indexOf("@")));
                        assignedEmail = email;
                        binding.getNewAddressButton.setVisibility(View.GONE);
                    } else {
                        binding.emailTextView.setText(getString(R.string.getting_temporary_email));
                        binding.emailUsernameEditText.setText("");
                    }
                });

        binding.emailTextView.setOnClickListener(v -> copyEmailToClipboard());
        binding.emailDomainTextView.setOnClickListener(v -> copyEmailToClipboard());

        binding.getNewAddressButton.setOnClickListener(v ->
                getNewAddress(binding.emailUsernameEditText.getText().toString() +
                        binding.emailDomainTextView.getText().toString()));

        binding.emailUsernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (assignedEmail != null) {
                    if (!assignedEmail.substring(0, assignedEmail.indexOf("@")).equals(s.toString())) {
                        binding.getNewAddressButton.setVisibility(View.VISIBLE);
                    } else {
                        binding.getNewAddressButton.setVisibility(View.GONE);
                    }
                }
            }
        });

        mainViewModel.getRefreshing().observe(this, refreshing -> {
            if (refreshing) {
                binding.refreshingSpinner.setVisibility(View.VISIBLE);
            } else {
                binding.refreshingSpinner.setVisibility(View.INVISIBLE);
            }
        });

        mainViewModel.getErrorLiveData().observe(this, errorEvent -> {
            if (errorEvent != null && !errorEvent.hasBeenHandled()) {
                String errorText = errorEvent.getContentIfNotHandled();
                if (errorText != null) {
                    Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getNewAddress(String newAddress) {
        if (isValidEmailAddress(newAddress)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(getString(R.string.confirm_getting_new_address, newAddress));
            builder.setIcon(R.drawable.ic_launcher_icon);
            builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                dialog.dismiss();
                mainViewModel.setEmailAddress(newAddress.substring(0, newAddress.indexOf("@")));
                binding.getNewAddressButton.setVisibility(View.GONE);
            });
            builder.setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmailAddress(String email) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void copyEmailToClipboard() {
        if (assignedEmail != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.app_name), assignedEmail);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.email_in_clipboard, Toast.LENGTH_SHORT).show();
        }
    }
}