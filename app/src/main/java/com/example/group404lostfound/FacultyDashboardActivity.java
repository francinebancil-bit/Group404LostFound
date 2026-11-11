package com.example.group404lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FacultyDashboardActivity extends AppCompatActivity {

    private static final String FACULTY_UID =
            "UB1zdrIz0yWncB2OR4Av4VXAM3o2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || !FACULTY_UID.equals(user.getUid())) {

            Toast.makeText(
                    this,
                    "Access denied. Faculty account only.",
                    Toast.LENGTH_LONG
            ).show();

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(
                    FacultyDashboardActivity.this,
                    WelcomeActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_faculty_dashboard);

        Button addItemButton = findViewById(R.id.addItemButton);
        Button manageItemsButton = findViewById(R.id.manageItemsButton);
        Button messagesButton = findViewById(R.id.messagesButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    FacultyDashboardActivity.this,
                    AddItemActivity.class
            );
            startActivity(intent);
        });

        manageItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    FacultyDashboardActivity.this,
                    ManageItemsActivity.class
            );
            startActivity(intent);
        });

        messagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    FacultyDashboardActivity.this,
                    FacultyMessagesActivity.class
            );
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Toast.makeText(
                    FacultyDashboardActivity.this,
                    "You have logged out successfully.",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    FacultyDashboardActivity.this,
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


