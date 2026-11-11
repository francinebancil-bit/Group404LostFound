package com.example.group404lostfound;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class FacultyMessagesActivity extends AppCompatActivity {

    private static final String FACULTY_UID =
            "UB1zdrIz0yWncB2OR4Av4VXAM3o2";

    private LinearLayout messagesContainer;
    private TextView noMessagesText;

    private FirebaseFirestore db;
    private ListenerRegistration messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_messages);

        messagesContainer = findViewById(R.id.messagesContainer);
        noMessagesText = findViewById(R.id.noMessagesText);

        if (FirebaseAuth.getInstance().getCurrentUser() == null
                || !FACULTY_UID.equals(
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {

            Toast.makeText(
                    this,
                    "Access denied. Faculty account required.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (db != null) {
            loadMessages();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (messagesListener != null) {
            messagesListener.remove();
            messagesListener = null;
        }
    }

    private void loadMessages() {

        messagesListener = db.collection("messages")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(this, (snapshots, error) -> {

                    if (error != null) {
                        Toast.makeText(
                                this,
                                "Unable to load messages: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    if (snapshots == null) {
                        return;
                    }

                    messagesContainer.removeAllViews();

                    if (snapshots.isEmpty()) {
                        noMessagesText.setText(
                                "No student messages yet."
                        );
                        noMessagesText.setVisibility(View.VISIBLE);
                        return;
                    }

                    noMessagesText.setVisibility(View.GONE);

                    for (DocumentSnapshot document : snapshots) {
                        addMessageCard(document);
                    }
                });
    }

    private void addMessageCard(DocumentSnapshot document) {

        final String documentId = document.getId();

        final String messageTitle =
                document.getString("title") == null
                        ? "" : document.getString("title");

        final String messageContent =
                document.getString("content") == null
                        ? "" : document.getString("content");

        final String studentEmail =
                document.getString("studentEmail") == null
                        ? "Unknown student"
                        : document.getString("studentEmail");

        final String savedReply =
                document.getString("reply") == null
                        ? "" : document.getString("reply");

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(12));
        background.setStroke(dp(1), Color.rgb(245, 205, 220));
        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(dp(12), dp(6), dp(12), dp(6));
        card.setLayoutParams(cardParams);

        TextView heading = new TextView(this);
        heading.setText("STUDENT INQUIRY");
        heading.setTextSize(13);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setTextColor(Color.rgb(201, 143, 157));
        heading.setPadding(0, 0, 0, dp(8));
        card.addView(heading);

        TextView emailText = new TextView(this);
        emailText.setText("Student email: " + studentEmail);
        emailText.setTextSize(13);
        emailText.setTextColor(Color.GRAY);
        emailText.setPadding(0, 0, 0, dp(8));
        card.addView(emailText);

        TextView messageText = new TextView(this);
        messageText.setText(
                "Title: " + messageTitle
                        + "\n\nMessage: " + messageContent
        );
        messageText.setTextSize(16);
        messageText.setTextColor(
                getResources().getColor(R.color.dark_gray)
        );
        card.addView(messageText);

        TextView replyText = new TextView(this);

        if (savedReply.trim().isEmpty()) {
            replyText.setText("Faculty reply: No reply yet");
            replyText.setTextColor(Color.GRAY);
        } else {
            replyText.setText("Faculty reply:\n" + savedReply);
            replyText.setTextColor(Color.rgb(80, 125, 90));
        }

        replyText.setTextSize(15);
        replyText.setPadding(0, dp(12), 0, dp(8));
        card.addView(replyText);

        Button replyButton = new Button(this);
        replyButton.setText(
                savedReply.trim().isEmpty() ? "Reply" : "Edit Reply"
        );

        replyButton.setOnClickListener(
                v -> showReplyDialog(documentId, savedReply)
        );

        card.addView(replyButton);

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete Message");
        deleteButton.setTextColor(Color.WHITE);

        deleteButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(190, 75, 85)
                )
        );

        deleteButton.setOnClickListener(
                v -> showDeleteConfirmation(documentId)
        );

        card.addView(deleteButton);

        messagesContainer.addView(card);
    }

    private void showDeleteConfirmation(String documentId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Message")
                .setMessage(
                        "Are you sure you want to delete this student message?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    db.collection("messages")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(
                                            this,
                                            "Message deleted successfully!",
                                            Toast.LENGTH_SHORT
                                    ).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            this,
                                            "Delete failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                })
                .show();
    }

    private void showReplyDialog(
            String documentId,
            String existingReply
    ) {

        EditText replyInput = new EditText(this);
        replyInput.setHint("Type your reply to the student");

        replyInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        replyInput.setMinLines(3);
        replyInput.setGravity(Gravity.TOP);
        replyInput.setText(existingReply);

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(dp(20), dp(8), dp(20), 0);
        dialogLayout.addView(replyInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reply to Student")
                .setView(dialogLayout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send Reply", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {

                            String reply = replyInput.getText()
                                    .toString().trim();

                            if (reply.isEmpty()) {
                                replyInput.setError(
                                        "Please enter a reply"
                                );
                                return;
                            }

                            Map<String, Object> updates =
                                    new HashMap<>();

                            updates.put("reply", reply);
                            updates.put(
                                    "updatedAt",
                                    com.google.firebase.firestore.FieldValue
                                            .serverTimestamp()
                            );

                            db.collection("messages")
                                    .document(documentId)
                                    .update(updates)
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(
                                                this,
                                                "Reply saved successfully!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(
                                                    this,
                                                    "Reply failed: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );
                        })
        );

        dialog.show();
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density
        );
    }
}