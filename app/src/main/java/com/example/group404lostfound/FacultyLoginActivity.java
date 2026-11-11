package com.example.group404lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FacultyLoginActivity extends AppCompatActivity {

    private EditText facultyUsername;
    private EditText facultyPassword;
    private Button facultyLoginButton;

    private FirebaseAuth mAuth;

    private static final String FACULTY_UID =
            "UB1zdrIz0yWncB2OR4Av4VXAM3o2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);

        facultyUsername = findViewById(R.id.facultyUsername);
        facultyPassword = findViewById(R.id.facultyPassword);
        facultyLoginButton = findViewById(R.id.facultyLoginButton);

        mAuth = FirebaseAuth.getInstance();

        facultyLoginButton.setOnClickListener(v -> loginFaculty());

        facultyPassword.setOnEditorActionListener(
                (textView, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        loginFaculty();
                        return true;
                    }
                    return false;
                }
        );
    }

    private void loginFaculty() {

        String email = facultyUsername.getText().toString().trim();
        String password = facultyPassword.getText().toString();

        if (email.isEmpty()) {
            facultyUsername.setError("Enter faculty email");
            facultyUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            facultyPassword.setError("Enter password");
            facultyPassword.requestFocus();
            return;
        }

        facultyLoginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    facultyLoginButton.setEnabled(true);

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null &&
                                FACULTY_UID.equals(user.getUid())) {

                            Toast.makeText(
                                    this,
                                    "Faculty login successful!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    FacultyLoginActivity.this,
                                    FacultyDashboardActivity.class
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            mAuth.signOut();

                            Toast.makeText(
                                    this,
                                    "Access denied. Faculty account only.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } else {

                        Toast.makeText(
                                this,
                                "Login failed. Check your faculty email and password.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
