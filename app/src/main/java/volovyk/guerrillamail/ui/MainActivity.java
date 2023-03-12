package volovyk.guerrillamail.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import dagger.hilt.android.AndroidEntryPoint;
import volovyk.guerrillamail.R;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        TextView emailTextView = findViewById(R.id.emailTextView);

        mainViewModel.getAssignedEmail().observe(this,
                s -> emailTextView.setText(getString(R.string.your_temporary_email, s)));

        emailTextView.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Email", mainViewModel.getAssignedEmail().getValue());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, R.string.email_in_clipboard, Toast.LENGTH_SHORT).show();
        });
    }
}