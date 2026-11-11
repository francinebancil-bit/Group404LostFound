package com.example.group404lostfound;

import android.content.Context;

import java.util.ArrayList;

public class FacultyMessages {

    public static ArrayList<String> messages = new ArrayList<>();
    public static ArrayList<String> replies = new ArrayList<>();

    public static void loadReplies(Context context) {
        replies.clear();
        replies.addAll(MessageStorage.loadReplies(context));

        while (replies.size() < messages.size()) {
            replies.add("");
        }
    }
    public static void deleteMessage(Context context, int position) {

        if (position < 0 || position >= messages.size()) {
            return;
        }

        messages.remove(position);

        if (position < replies.size()) {
            replies.remove(position);
        }

        MessageStorage.saveMessages(context, messages);
        MessageStorage.saveReplies(context, replies);
    }
    public static void setReply(
            Context context,
            int position,
            String reply
    ) {
        if (position < 0 || position >= messages.size()) {
            return;
        }

        while (replies.size() < messages.size()) {
            replies.add("");
        }

        replies.set(position, reply);
        MessageStorage.saveReplies(context, replies);
    }

    public static String getReply(int position) {
        if (position >= 0 && position < replies.size()) {
            return replies.get(position);
        }

        return "";
    }
}

