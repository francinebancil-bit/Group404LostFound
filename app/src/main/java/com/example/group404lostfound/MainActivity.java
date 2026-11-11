package com.example.group404lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button searchButton;
    private Button viewItemsButton;
    private Button studentMessagesButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = findViewById(R.id.searchButton);
        viewItemsButton = findViewById(R.id.viewItemsButton);
        studentMessagesButton = findViewById(R.id.studentMessagesButton);
        logoutButton = findViewById(R.id.logoutButton);

        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SearchActivity.class
            );
            startActivity(intent);
        });

        viewItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ItemsActivity.class
            );
            startActivity(intent);
        });

        studentMessagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    StudentMessagesActivity.class
            );
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Toast.makeText(
                    MainActivity.this,
                    "You have logged out successfully.",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    MainActivity.this,
                    WelcomeActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }
}
