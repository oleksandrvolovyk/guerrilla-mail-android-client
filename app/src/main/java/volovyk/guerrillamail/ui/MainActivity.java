package volovyk.guerrillamail.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import volovyk.guerrillamail.R;
import volovyk.guerrillamail.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private String assignedEmail;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getAssignedEmail().observe(this,
                email -> {
                    binding.emailTextView.setText(getString(R.string.your_temporary_email, email));
                    assignedEmail = email;
                });

        binding.emailTextView.setOnClickListener(v -> copyEmailToClipboard());
    }

    private void copyEmailToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.app_name), assignedEmail);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.email_in_clipboard, Toast.LENGTH_SHORT).show();
    }
}