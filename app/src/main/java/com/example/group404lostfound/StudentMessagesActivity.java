package com.example.group404lostfound;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class StudentMessagesActivity extends AppCompatActivity {

    private EditText messageTitle;
    private EditText messageContent;
    private LinearLayout messagesContainer;
    private TextView noMessagesText;
    private Button sendMessageButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration messagesListener;

    private String studentUid;
    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_messages);

        messageTitle = findViewById(R.id.messageTitle);
        messageContent = findViewById(R.id.messageContent);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messagesContainer = findViewById(R.id.messagesContainer);
        noMessagesText = findViewById(R.id.noMessagesText);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(
                    this,
                    "Please log in first.",
                    Toast.LENGTH_SHORT
            ).show();

            startActivity(new Intent(
                    this,
                    StudentLoginActivity.class
            ));
            finish();
            return;
        }

        studentUid = user.getUid();
        studentEmail = user.getEmail();

        sendMessageButton.setOnClickListener(v -> sendMessage());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (studentUid != null) {
            listenToMessages();
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

    private void listenToMessages() {

        messagesListener = db.collection("messages")
                .whereEqualTo("studentUid", studentUid)
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
                                "No messages yet. Send an inquiry to faculty."
                        );
                        noMessagesText.setVisibility(View.VISIBLE);
                        return;
                    }

                    noMessagesText.setVisibility(View.GONE);

                    for (DocumentSnapshot document : snapshots) {

                        String title = document.getString("title");
                        String content = document.getString("content");
                        String reply = document.getString("reply");

                        String message = "Title: "
                                + (title == null ? "" : title)
                                + "\n\nMessage: "
                                + (content == null ? "" : content);

                        Timestamp updatedAt =
                                document.getTimestamp("updatedAt");

                        Timestamp studentReadAt =
                                document.getTimestamp("studentReadAt");

                        boolean hasReply =
                                reply != null && !reply.trim().isEmpty();

                        boolean isNewReply = hasReply &&
                                (studentReadAt == null ||
                                        (updatedAt != null &&
                                                updatedAt.compareTo(studentReadAt) > 0));

                        addMessageCard(
                                document.getId(),
                                message,
                                reply,
                                isNewReply
                        );
                    }
                });
    }

    private void sendMessage() {

        String title = messageTitle.getText().toString().trim();
        String content = messageContent.getText().toString().trim();

        if (title.isEmpty()) {
            messageTitle.setError("Enter a message title");
            messageTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            messageContent.setError("Enter your message");
            messageContent.requestFocus();
            return;
        }

        if (studentEmail == null || studentEmail.isEmpty()) {
            Toast.makeText(
                    this,
                    "Your account email is unavailable. Please log in again.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        sendMessageButton.setEnabled(false);

        Map<String, Object> messageData = new HashMap<>();

        messageData.put("studentUid", studentUid);
        messageData.put("studentEmail", studentEmail);
        messageData.put("title", title);
        messageData.put("content", content);
        messageData.put("reply", "");
        messageData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {

                    sendMessageButton.setEnabled(true);

                    messageTitle.setText("");
                    messageContent.setText("");

                    Toast.makeText(
                            this,
                            "Message sent successfully!",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    sendMessageButton.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Failed to send message: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void addMessageCard(
            String documentId,
            String message,
            String reply,
            boolean isNewReply
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(12));
        background.setStroke(dp(1), Color.rgb(245, 205, 220));
        card.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(dp(12), dp(6), dp(12), dp(6));
        card.setLayoutParams(params);

        TextView heading = new TextView(this);
        heading.setText("YOUR INQUIRY");
        heading.setTextSize(13);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setTextColor(Color.rgb(201, 143, 157));
        heading.setPadding(0, 0, 0, dp(8));
        card.addView(heading);

        TextView messageText = new TextView(this);
        messageText.setText(message == null ? "" : message);
        messageText.setTextSize(16);
        messageText.setTextColor(
                getResources().getColor(R.color.dark_gray)
        );
        card.addView(messageText);

        TextView replyText = new TextView(this);

        if (reply == null || reply.trim().isEmpty()) {

            replyText.setText(
                    "Faculty reply: Waiting for a response."
            );
            replyText.setTextColor(Color.GRAY);

        } else {

            replyText.setText("Faculty reply:\n" + reply);
            replyText.setTextColor(Color.rgb(80, 125, 90));
        }

        replyText.setTextSize(15);
        replyText.setPadding(0, dp(12), 0, 0);
        card.addView(replyText);

        if (isNewReply) {

            TextView newReplyLabel = new TextView(this);

            newReplyLabel.setText("NEW REPLY");
            newReplyLabel.setTextSize(12);
            newReplyLabel.setTypeface(null, Typeface.BOLD);
            newReplyLabel.setTextColor(Color.WHITE);
            newReplyLabel.setGravity(android.view.Gravity.CENTER);
            newReplyLabel.setPadding(
                    dp(12), dp(7), dp(12), dp(7)
            );

            GradientDrawable labelBackground = new GradientDrawable();
            labelBackground.setColor(Color.rgb(201, 143, 157));
            labelBackground.setCornerRadius(dp(8));

            newReplyLabel.setBackground(labelBackground);

            LinearLayout.LayoutParams labelParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            labelParams.topMargin = dp(12);
            newReplyLabel.setLayoutParams(labelParams);

            card.addView(newReplyLabel);

            Button markReadButton = new Button(this);
            markReadButton.setText("Mark as Read");
            markReadButton.setTextSize(12);

            LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            buttonParams.topMargin = dp(6);
            markReadButton.setLayoutParams(buttonParams);

            markReadButton.setOnClickListener(v -> {

                markReadButton.setEnabled(false);

                db.collection("messages")
                        .document(documentId)
                        .update(
                                "studentReadAt",
                                FieldValue.serverTimestamp()
                        )
                        .addOnSuccessListener(unused -> {

                            Toast.makeText(
                                    this,
                                    "Reply marked as read.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        })
                        .addOnFailureListener(e -> {

                            markReadButton.setEnabled(true);

                            Toast.makeText(
                                    this,
                                    "Unable to mark as read: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            });

            card.addView(markReadButton);
        }

        messagesContainer.addView(card);
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density
        );
    }
}
