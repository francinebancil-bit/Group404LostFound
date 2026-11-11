package com.example.group404lostfound;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Set;

public class MessageStorage {

    private static final String PREF_NAME = "lost_found_messages";
    private static final String KEY_MESSAGES = "messages";
    private static final String KEY_REPLIES = "replies";

    public static void saveMessages(
            Context context,
            ArrayList<String> messages
    ) {
        saveList(context, KEY_MESSAGES, messages);
    }

    public static ArrayList<String> loadMessages(Context context) {
        return loadList(context, KEY_MESSAGES);
    }

    public static void saveReplies(
            Context context,
            ArrayList<String> replies
    ) {
        saveList(context, KEY_REPLIES, replies);
    }

    public static ArrayList<String> loadReplies(Context context) {
        return loadList(context, KEY_REPLIES);
    }

    private static void saveList(
            Context context,
            String key,
            ArrayList<String> items
    ) {
        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        JSONArray jsonArray = new JSONArray();

        for (String item : items) {
            jsonArray.put(item == null ? "" : item);
        }

        preferences.edit()
                .putString(key, jsonArray.toString())
                .apply();
    }

    private static ArrayList<String> loadList(
            Context context,
            String key
    ) {
        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        ArrayList<String> items = new ArrayList<>();

        Object savedValue = preferences.getAll().get(key);

        if (savedValue == null) {
            return items;
        }

        // Handle the current JSON String format.
        if (savedValue instanceof String) {

            try {
                JSONArray jsonArray =
                        new JSONArray((String) savedValue);

                for (int i = 0; i < jsonArray.length(); i++) {
                    items.add(jsonArray.optString(i, ""));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Handle old saved Set / HashSet format.
        else if (savedValue instanceof Set<?>) {

            for (Object item : (Set<?>) savedValue) {
                items.add(item == null ? "" : item.toString());
            }

            // Convert old data into the current JSON format.
            saveList(context, key, items);
        }

        return items;
    }
}