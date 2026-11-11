package com.example.group404lostfound;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemStorage {

    private static final String PREF_NAME = "LostFoundStorage";
    private static final String ITEMS_KEY = "items";

    // Save items
    public static void saveItems(
            Context context,
            ArrayList<LostFoundItem> items
    ) {

        try {
            JSONArray jsonArray = new JSONArray();

            for (LostFoundItem item : items) {

                JSONObject object = new JSONObject();

                object.put("name", safeValue(item.getName()));
                object.put("description", safeValue(item.getDescription()));
                object.put("category", safeValue(item.getCategory()));
                object.put("date", safeValue(item.getDate()));
                object.put("location", safeValue(item.getLocation()));
                object.put("type", safeValue(item.getType()));
                object.put("photoUri", safeValue(item.getPhotoUri()));
                object.put("status", safeValue(item.getStatus()));

                jsonArray.put(object);
            }

            SharedPreferences preferences =
                    context.getSharedPreferences(
                            PREF_NAME,
                            Context.MODE_PRIVATE
                    );

            preferences.edit()
                    .putString(ITEMS_KEY, jsonArray.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load saved items
    public static ArrayList<LostFoundItem> loadItems(Context context) {

        ArrayList<LostFoundItem> items = new ArrayList<>();

        try {
            SharedPreferences preferences =
                    context.getSharedPreferences(
                            PREF_NAME,
                            Context.MODE_PRIVATE
                    );

            String savedData = preferences.getString(ITEMS_KEY, "");

            if (savedData == null || savedData.trim().isEmpty()) {
                return items;
            }

            JSONArray jsonArray = new JSONArray(savedData);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                String name = object.optString("name", "");
                String description = object.optString("description", "");
                String category = object.optString("category", "");
                String date = object.optString("date", "");
                String location = object.optString("location", "");
                String type = object.optString("type", "Lost");
                String photoUri = object.optString("photoUri", "");
                String status = object.optString("status", "Available");

                LostFoundItem item = new LostFoundItem(
                        name,
                        description,
                        category,
                        date,
                        location,
                        type,
                        photoUri,
                        status
                );

                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    // Prevent null values
    private static String safeValue(String value) {
        return value == null ? "" : value;
    }
}


