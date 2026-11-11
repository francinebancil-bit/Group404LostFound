package com.example.group404lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button studentButton;
    private Button facultyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        studentButton = findViewById(R.id.studentButton);
        facultyButton = findViewById(R.id.facultyButton);

        studentButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    WelcomeActivity.this,
                    StudentLoginActivity.class
            );
            startActivity(intent);
        });

        facultyButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    WelcomeActivity.this,
                    FacultyLoginActivity.class
            );
            startActivity(intent);
        });
    }
}
