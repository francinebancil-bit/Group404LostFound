package com.example.group404lostfound;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private LinearLayout resultsContainer;
    private TextView noResultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchInput);
        resultsContainer = findViewById(R.id.resultsContainer);
        noResultsText = findViewById(R.id.noResultsText);

        Button searchButton = findViewById(R.id.searchButton);

        noResultsText.setVisibility(View.GONE);

        searchButton.setOnClickListener(v -> searchItems());
    }

    private void loadItems() {
        AddItemActivity.itemList.clear();
        AddItemActivity.itemList.addAll(
                ItemStorage.loadItems(this)
        );
    }

    private void searchItems() {
        String keyword = searchInput.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        resultsContainer.removeAllViews();
        noResultsText.setVisibility(View.GONE);

        if (keyword.isEmpty()) {
            searchInput.setError("Please enter an item to search");
            searchInput.requestFocus();
            return;
        }

        loadItems();

        int matchCount = 0;

        for (LostFoundItem item : AddItemActivity.itemList) {

            if (item == null) {
                continue;
            }

            if (matches(item.getName(), keyword)
                    || matches(item.getDescription(), keyword)
                    || matches(item.getCategory(), keyword)
                    || matches(item.getLocation(), keyword)
                    || matches(item.getType(), keyword)
                    || matches(item.getStatus(), keyword)) {

                displayResult(item);
                matchCount++;
            }
        }

        if (matchCount == 0) {
            noResultsText.setText("No matching items found.");
            noResultsText.setVisibility(View.VISIBLE);
        }
    }

    private boolean matches(String value, String keyword) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private void displayResult(LostFoundItem item) {

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

        cardParams.setMargins(dp(12), dp(8), dp(12), dp(8));
        card.setLayoutParams(cardParams);

        String photoUri = item.getPhotoUri();

        if (photoUri != null && !photoUri.trim().isEmpty()) {

            ImageView image = new ImageView(this);

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(200)
                    );

            image.setLayoutParams(imageParams);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            try {
                image.setImageURI(Uri.parse(photoUri));

                if (image.getDrawable() == null) {
                    image.setImageResource(
                            android.R.drawable.ic_menu_gallery
                    );
                }
            } catch (Exception e) {
                image.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }

            card.addView(image);
        }

        TextView itemName = new TextView(this);
        itemName.setText(safeText(item.getName()));
        itemName.setTextSize(20);
        itemName.setTypeface(null, android.graphics.Typeface.BOLD);
        itemName.setTextColor(Color.rgb(201, 143, 157));
        itemName.setPadding(0, dp(12), 0, dp(8));
        card.addView(itemName);

        TextView resultText = new TextView(this);

        String information =
                "Description: " + safeText(item.getDescription()) + "\n\n"
                        + "Category: " + safeText(item.getCategory()) + "\n"
                        + "Date: " + safeText(item.getDate()) + "\n"
                        + "Location: " + safeText(item.getLocation()) + "\n"
                        + "Type: " + safeText(item.getType()) + "\n"
                        + "Status: " + safeText(item.getStatus());

        resultText.setText(information);
        resultText.setTextSize(15);
        resultText.setTextColor(
                getResources().getColor(R.color.dark_gray)
        );

        card.addView(resultText);

        resultsContainer.addView(card);
    }
    private String safeText(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not provided"
                : value;
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (resultsContainer != null) {
            resultsContainer.removeAllViews();
        }

        if (noResultsText != null) {
            noResultsText.setVisibility(View.GONE);
        }
    }
}

