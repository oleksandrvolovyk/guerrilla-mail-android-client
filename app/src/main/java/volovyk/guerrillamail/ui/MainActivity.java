package volovyk.guerrillamail.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.TextView;

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
    }
}