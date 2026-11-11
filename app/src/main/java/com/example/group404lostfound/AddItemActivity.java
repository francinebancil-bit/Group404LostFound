package com.example.group404lostfound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    public static ArrayList<LostFoundItem> itemList = new ArrayList<>();

    private Uri selectedImageUri;

    private EditText itemName;
    private EditText itemDescription;
    private EditText itemCategory;
    private EditText itemDate;
    private EditText itemLocation;

    private Spinner itemTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item2);

        itemList.clear();
        itemList.addAll(ItemStorage.loadItems(this));

        itemName = findViewById(R.id.itemNameInput);
        itemDescription = findViewById(R.id.itemDescriptionInput);
        itemCategory = findViewById(R.id.itemCategoryInput);
        itemDate = findViewById(R.id.itemDateInput);
        itemLocation = findViewById(R.id.itemLocationInput);

        itemTypeSpinner = findViewById(R.id.itemTypeSpinner);

        Button photoButton = findViewById(R.id.photoButton);
        Button saveItemButton = findViewById(R.id.saveItemButton);

        setupTypeSpinner();

        photoButton.setOnClickListener(v -> openGallery());

        saveItemButton.setOnClickListener(v -> saveItem());
    }

    private void setupTypeSpinner() {

        String[] typeOptions = {
                "Lost",
                "Found"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typeOptions
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        itemTypeSpinner.setAdapter(adapter);
    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );

        startActivityForResult(intent, PICK_IMAGE);
    }

    private void saveItem() {

        String name = itemName.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String category = itemCategory.getText().toString().trim();
        String date = itemDate.getText().toString().trim();
        String location = itemLocation.getText().toString().trim();

        String type = itemTypeSpinner.getSelectedItem().toString();

        // Validate required fields.
        if (name.isEmpty()) {
            itemName.setError("Enter item name");
            itemName.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            itemDescription.setError("Enter description");
            itemDescription.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            itemCategory.setError("Enter category");
            itemCategory.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            itemDate.setError("Enter date");
            itemDate.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            itemLocation.setError("Enter location");
            itemLocation.requestFocus();
            return;
        }

        for (LostFoundItem existingItem : itemList) {

            if (existingItem == null) {
                continue;
            }

            if (safeEquals(existingItem.getName(), name)
                    && safeEquals(existingItem.getDescription(), description)
                    && safeEquals(existingItem.getCategory(), category)
                    && safeEquals(existingItem.getDate(), date)
                    && safeEquals(existingItem.getLocation(), location)
                    && safeEquals(existingItem.getType(), type)) {

                Toast.makeText(
                        this,
                        "This item is already added.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }

        String photoUri = "";

        if (selectedImageUri != null) {
            photoUri = selectedImageUri.toString();
        }

        String status = "Available";

        LostFoundItem newItem = new LostFoundItem(
                name,
                description,
                category,
                date,
                location,
                type,
                photoUri,
                status
        );

        itemList.add(newItem);

        ItemStorage.saveItems(this, itemList);

        Toast.makeText(
                this,
                "Item saved successfully!",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    private boolean safeEquals(String first, String second) {

        if (first == null) {
            first = "";
        }

        if (second == null) {
            second = "";
        }

        return first.equalsIgnoreCase(second);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null) {

            selectedImageUri = data.getData();

            if (selectedImageUri != null) {

                try {
                    getContentResolver().takePersistableUriPermission(
                            selectedImageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

                } catch (Exception e) {

                }

                Toast.makeText(
                        this,
                        "Photo selected successfully!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}





