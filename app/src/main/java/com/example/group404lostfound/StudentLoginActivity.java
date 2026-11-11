package com.example.group404lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StudentLoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signUpText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> loginStudent());

        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(
                    StudentLoginActivity.this,
                    StudentSignUpActivity.class
            );
            startActivity(intent);
        });
    }

    private void loginStudent() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please enter your Gmail and password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {

                        Toast.makeText(this,
                                "Login successful! Welcome!",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                StudentLoginActivity.this,
                                MainActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(this,
                                "Login failed. Please check your Gmail and password.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}

