package com.example.group404lostfound;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ManageItemsActivity extends AppCompatActivity {

    private LinearLayout itemsContainer;
    private TextView noItemsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items2);

        itemsContainer = findViewById(R.id.itemsContainer);
        noItemsText = findViewById(R.id.noManageItemsText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
        displayItems();
    }

    private void loadItems() {
        AddItemActivity.itemList.clear();
        AddItemActivity.itemList.addAll(
                ItemStorage.loadItems(this)
        );
    }

    private void displayItems() {
        itemsContainer.removeAllViews();

        if (AddItemActivity.itemList.isEmpty()) {
            noItemsText.setVisibility(View.VISIBLE);
            return;
        }

        noItemsText.setVisibility(View.GONE);

        for (int i = 0; i < AddItemActivity.itemList.size(); i++) {
            LostFoundItem item = AddItemActivity.itemList.get(i);
            displayItem(item, i);
        }
    }

    private void displayItem(LostFoundItem item, int position) {

        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(20, 20, 20, 20);

        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setColor(Color.WHITE);
        cardBackground.setCornerRadius(25);
        cardBackground.setStroke(
                2,
                Color.rgb(245, 205, 220)
        );

        itemLayout.setBackground(cardBackground);

        // Display item photo
        if (item.getPhotoUri() != null &&
                !item.getPhotoUri().isEmpty()) {

            ImageView itemImage = new ImageView(this);

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            300
                    );

            imageParams.setMargins(0, 0, 0, 15);
            itemImage.setLayoutParams(imageParams);
            itemImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            itemImage.setAdjustViewBounds(true);

            try {
                Uri imageUri = Uri.parse(item.getPhotoUri());
                itemImage.setImageURI(imageUri);
            } catch (Exception e) {
                itemImage.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }

            itemLayout.addView(itemImage);
        }

        // Item name
        TextView itemNameText = new TextView(this);
        itemNameText.setText(item.getName());
        itemNameText.setTextSize(20);
        itemNameText.setTextColor(Color.rgb(70, 70, 70));
        itemNameText.setTypeface(null, Typeface.BOLD);
        itemNameText.setPadding(5, 10, 5, 10);

        itemLayout.addView(itemNameText);

        // Item information
        TextView itemText = new TextView(this);

        String information =
                "Description: " + item.getDescription() +
                        "\n\nCategory: " + item.getCategory() +
                        "\nDate: " + item.getDate() +
                        "\nLocation: " + item.getLocation() +
                        "\nType: " + item.getType() +
                        "\nStatus: " + item.getStatus();

        itemText.setText(information);
        itemText.setTextSize(15);
        itemText.setTextColor(
                getResources().getColor(R.color.dark_gray)
        );
        itemText.setPadding(5, 5, 5, 15);

        itemLayout.addView(itemText);

        // Update Status button
        Button statusButton = new Button(this);
        statusButton.setText("UPDATE STATUS");
        statusButton.setTextSize(14);
        statusButton.setTextColor(Color.WHITE);
        statusButton.setBackgroundTintList(
                getResources().getColorStateList(
                        R.color.pink_primary
                )
        );

        statusButton.setOnClickListener(
                v -> updateItemStatus(position)
        );

        itemLayout.addView(statusButton);

        // Edit Item button
        Button editButton = new Button(this);
        editButton.setText("EDIT ITEM");
        editButton.setTextSize(14);
        editButton.setTextColor(Color.WHITE);
        editButton.setBackgroundTintList(
                getResources().getColorStateList(
                        R.color.pink_primary
                )
        );

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ManageItemsActivity.this,
                    EditItemActivity.class
            );

            intent.putExtra("itemPosition", position);
            startActivity(intent);
        });

        itemLayout.addView(editButton);

        // Delete Item button
        Button deleteButton = new Button(this);
        deleteButton.setText("DELETE ITEM");
        deleteButton.setTextSize(14);
        deleteButton.setTextColor(Color.WHITE);
        deleteButton.setBackgroundTintList(
                getResources().getColorStateList(
                        R.color.pink_primary
                )
        );

        deleteButton.setOnClickListener(
                v -> deleteItem(position)
        );

        itemLayout.addView(deleteButton);

        // Card spacing
        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 25);
        itemLayout.setLayoutParams(cardParams);

        itemsContainer.addView(itemLayout);
    }

    // Update and save item status
    private void updateItemStatus(int position) {

        if (position < 0 ||
                position >= AddItemActivity.itemList.size()) {
            return;
        }

        LostFoundItem item =
                AddItemActivity.itemList.get(position);

        String[] statuses = {
                "Available",
                "Claimed",
                "Returned"
        };

        new AlertDialog.Builder(this)
                .setTitle("Update Item Status")
                .setItems(statuses, (dialog, which) -> {

                    String selectedStatus = statuses[which];

                    item.setStatus(selectedStatus);

                    ItemStorage.saveItems(
                            this,
                            AddItemActivity.itemList
                    );

                    Toast.makeText(
                            this,
                            "Status updated to " + selectedStatus,
                            Toast.LENGTH_SHORT
                    ).show();

                    displayItems();
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    // Delete item with confirmation
    private void deleteItem(int position) {

        if (position < 0 ||
                position >= AddItemActivity.itemList.size()) {
            return;
        }

        LostFoundItem item =
                AddItemActivity.itemList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage(
                        "Are you sure you want to delete \"" +
                                item.getName() + "\"?"
                )
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DELETE", (dialog, which) -> {

                    AddItemActivity.itemList.remove(position);

                    ItemStorage.saveItems(
                            this,
                            AddItemActivity.itemList
                    );

                    Toast.makeText(
                            this,
                            "Item deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    displayItems();
                })
                .show();
    }
}



