package com.example.group404lostfound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 200;

    private EditText itemName;
    private EditText itemDescription;
    private EditText itemCategory;
    private EditText itemDate;
    private EditText itemLocation;

    private Spinner itemTypeSpinner;

    private ImageView itemImagePreview;

    private LostFoundItem item;

    private Uri selectedImageUri;

    private int itemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_edit_item
        );


        itemImagePreview =
                findViewById(
                        R.id.itemImagePreview
                );

        itemName =
                findViewById(
                        R.id.itemNameInput
                );

        itemDescription =
                findViewById(
                        R.id.itemDescriptionInput
                );

        itemCategory =
                findViewById(
                        R.id.itemCategoryInput
                );

        itemDate =
                findViewById(
                        R.id.itemDateInput
                );

        itemLocation =
                findViewById(
                        R.id.itemLocationInput
                );

        itemTypeSpinner =
                findViewById(
                        R.id.itemTypeSpinner
                );


        Button photoButton =
                findViewById(
                        R.id.photoButton
                );

        Button saveButton =
                findViewById(
                        R.id.saveEditButton
                );


        itemPosition = getIntent().getIntExtra(
                "itemPosition",
                -1
        );

        AddItemActivity.itemList.clear();
        AddItemActivity.itemList.addAll(
                ItemStorage.loadItems(this)
        );

        if (itemPosition < 0 ||
                itemPosition >= AddItemActivity.itemList.size()) {

            Toast.makeText(
                    this,
                    "Item not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }


        item =
                AddItemActivity.itemList.get(
                        itemPosition
                );


        setupTypeSpinner();

        loadItemData();


        photoButton.setOnClickListener(
                v -> openGallery()
        );


        saveButton.setOnClickListener(
                v -> saveChanges()
        );
    }



    private void setupTypeSpinner() {

        String[] typeOptions = {
                "Lost",
                "Found"
        };


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        typeOptions
                );


        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        itemTypeSpinner.setAdapter(
                adapter
        );
    }


    private void loadItemData() {

        itemName.setText(
                item.getName()
        );


        itemDescription.setText(
                item.getDescription()
        );


        itemCategory.setText(
                item.getCategory()
        );


        itemDate.setText(
                item.getDate()
        );


        itemLocation.setText(
                item.getLocation()
        );


        setSpinnerValue(
                itemTypeSpinner,
                item.getType()
        );


        if (item.getPhotoUri() != null &&
                !item.getPhotoUri().isEmpty()) {

            try {

                selectedImageUri =
                        Uri.parse(
                                item.getPhotoUri()
                        );


                itemImagePreview.setImageURI(
                        selectedImageUri
                );

            } catch (Exception e) {

                itemImagePreview.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }
        }
    }



    private void setSpinnerValue(
            Spinner spinner,
            String value) {

        if (value == null) {
            return;
        }


        ArrayAdapter<?> adapter =
                (ArrayAdapter<?>) spinner.getAdapter();


        for (int i = 0;
             i < adapter.getCount();
             i++) {

            Object spinnerValue =
                    adapter.getItem(i);


            if (spinnerValue != null &&
                    spinnerValue.toString()
                            .equalsIgnoreCase(value)) {

                spinner.setSelection(i);

                break;
            }
        }
    }


    private void openGallery() {

        Intent intent =
                new Intent(
                        Intent.ACTION_OPEN_DOCUMENT
                );


        intent.setType(
                "image/*"
        );


        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );


        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );


        startActivityForResult(
                intent,
                PICK_IMAGE
        );
    }


    private void saveChanges() {

        String name =
                itemName.getText()
                        .toString()
                        .trim();


        String description =
                itemDescription.getText()
                        .toString()
                        .trim();


        String category =
                itemCategory.getText()
                        .toString()
                        .trim();


        String date =
                itemDate.getText()
                        .toString()
                        .trim();


        String location =
                itemLocation.getText()
                        .toString()
                        .trim();


        String type =
                itemTypeSpinner
                        .getSelectedItem()
                        .toString();


        if (name.isEmpty()) {

            itemName.setError(
                    "Enter item name"
            );

            itemName.requestFocus();

            return;
        }


        if (description.isEmpty()) {

            itemDescription.setError(
                    "Enter description"
            );

            itemDescription.requestFocus();

            return;
        }


        if (category.isEmpty()) {

            itemCategory.setError(
                    "Enter category"
            );

            itemCategory.requestFocus();

            return;
        }


        if (date.isEmpty()) {

            itemDate.setError(
                    "Enter date"
            );

            itemDate.requestFocus();

            return;
        }


        if (location.isEmpty()) {

            itemLocation.setError(
                    "Enter location"
            );

            itemLocation.requestFocus();

            return;
        }

        item.setName(
                name
        );


        item.setDescription(
                description
        );


        item.setCategory(
                category
        );


        item.setDate(
                date
        );


        item.setLocation(
                location
        );


        item.setType(
                type
        );


        if (selectedImageUri != null) {

            item.setPhotoUri(
                    selectedImageUri.toString()
            );
        }


        ItemStorage.saveItems(
                this,
                AddItemActivity.itemList
        );


        Toast.makeText(
                this,
                "Item updated successfully!",
                Toast.LENGTH_SHORT
        ).show();


        finish();
    }



    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );


        if (requestCode == PICK_IMAGE &&
                resultCode == RESULT_OK &&
                data != null) {

            selectedImageUri =
                    data.getData();


            if (selectedImageUri != null) {

                try {

                    getContentResolver()
                            .takePersistableUriPermission(
                                    selectedImageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );

                } catch (Exception e) {

                }

                itemImagePreview.setImageURI(
                        selectedImageUri
                );


                Toast.makeText(
                        this,
                        "Photo changed successfully!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}





