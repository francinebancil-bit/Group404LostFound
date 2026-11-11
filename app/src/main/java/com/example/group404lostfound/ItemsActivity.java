package com.example.group404lostfound;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ItemsActivity extends AppCompatActivity {

    private LinearLayout itemsContainer;
    private TextView noItemsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_items2);

        itemsContainer = findViewById(R.id.itemsContainer);
        noItemsText = findViewById(R.id.noItemsText);

        displayItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (itemsContainer != null && noItemsText != null) {
            displayItems();
        }
    }

    private void loadItems() {
        AddItemActivity.itemList.clear();

        AddItemActivity.itemList.addAll(
                ItemStorage.loadItems(this)
        );
    }

    private void displayItems() {
        loadItems();

        itemsContainer.removeAllViews();

        if (AddItemActivity.itemList.isEmpty()) {
            noItemsText.setText(
                    "No lost or found items available yet."
            );

            noItemsText.setVisibility(View.VISIBLE);
            return;
        }

        noItemsText.setVisibility(View.GONE);

        for (LostFoundItem item : AddItemActivity.itemList) {
            displayItemCard(item);
        }
    }

    private void displayItemCard(LostFoundItem item) {

        // Create item card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);

        int padding = dp(16);

        card.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setColor(Color.WHITE);
        cardBackground.setCornerRadius(dp(16));
        cardBackground.setStroke(
                dp(1),
                Color.rgb(245, 205, 220)
        );

        card.setBackground(cardBackground);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                dp(12),
                dp(8),
                dp(12),
                dp(12)
        );

        card.setLayoutParams(cardParams);

        if (item.getPhotoUri() != null
                && !item.getPhotoUri().trim().isEmpty()) {

            ImageView itemImage = new ImageView(this);

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(220)
                    );

            imageParams.setMargins(0, 0, 0, dp(12));

            itemImage.setLayoutParams(imageParams);
            itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            try {
                Uri imageUri = Uri.parse(item.getPhotoUri());
                itemImage.setImageURI(imageUri);

                if (itemImage.getDrawable() == null) {
                    itemImage.setImageResource(
                            android.R.drawable.ic_menu_gallery
                    );
                }

            } catch (Exception e) {
                itemImage.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }

            card.addView(itemImage);
        }

        TextView nameText = new TextView(this);

        nameText.setText(safeText(item.getName()));
        nameText.setTextSize(20);
        nameText.setTextColor(Color.rgb(70, 70, 70));
        nameText.setTypeface(null, Typeface.BOLD);

        nameText.setPadding(0, 0, 0, dp(10));

        card.addView(nameText);

        TextView detailsText = new TextView(this);

        String information =
                "Description: " + safeText(item.getDescription())
                        + "\n\nCategory: " + safeText(item.getCategory())
                        + "\nDate: " + safeText(item.getDate())
                        + "\nLocation: " + safeText(item.getLocation())
                        + "\nType: " + safeText(item.getType());

        detailsText.setText(information);
        detailsText.setTextSize(15);
        detailsText.setTextColor(
                getResources().getColor(R.color.dark_gray)
        );

        detailsText.setPadding(0, 0, 0, dp(12));

        card.addView(detailsText);

        TextView statusText = new TextView(this);

        String status = safeText(item.getStatus());

        statusText.setText("Status: " + status);
        statusText.setTextSize(15);
        statusText.setTypeface(null, Typeface.BOLD);
        statusText.setTextColor(getStatusColor(status));

        statusText.setPadding(0, dp(4), 0, dp(4));

        card.addView(statusText);

        itemsContainer.addView(card);
    }

    private int getStatusColor(String status) {

        if ("Claimed".equalsIgnoreCase(status)) {
            return Color.rgb(142, 154, 175);
        }

        if ("Returned".equalsIgnoreCase(status)) {
            return Color.rgb(100, 145, 110);
        }

        return Color.rgb(201, 143, 157);
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not provided"
                : value.trim();
    }
}







