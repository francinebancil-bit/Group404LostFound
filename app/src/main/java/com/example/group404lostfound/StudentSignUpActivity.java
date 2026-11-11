package com.example.group404lostfound;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StudentSignUpActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button signUpButton;
    private TextView loginText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_signup);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signUpButton = findViewById(R.id.signUpButton);
        loginText = findViewById(R.id.loginText);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(v -> registerStudent());

        loginText.setOnClickListener(v -> finish());
    }

    private void registerStudent() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()
                || !email.toLowerCase().endsWith("@gmail.com")) {
            Toast.makeText(this, "Please enter a valid Gmail address.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must have at least 6 characters.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        signUpButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    signUpButton.setEnabled(true);

                    if (task.isSuccessful()) {

                        Toast.makeText(this,
                                "Account created successfully!",
                                Toast.LENGTH_LONG).show();

                        finish();

                    } else {

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed.";

                        Toast.makeText(this, error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}

