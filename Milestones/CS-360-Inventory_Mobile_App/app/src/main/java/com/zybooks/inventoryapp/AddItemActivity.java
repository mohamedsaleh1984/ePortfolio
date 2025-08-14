package com.zybooks.inventoryapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.rpc.Help;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.Item;
import com.zybooks.inventoryapp.model.ValidationResult;
import com.zybooks.inventoryapp.utils.FirebaseHelper;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

///
/// Add/Edit Item Activity
///
public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private EditText edItemName, edItemQty, edItemPrice;
    private Button btnAddEdit, btnCancel;
    private String ItemID = "";
    private FirebaseFirestore db;

    /**
     * Initial call in activity lifecycle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Bind UI Views to UI objects
        initViews();

        // reference to FireStore
        db = FirebaseHelper.getInstance().getFirestore();

        // Handle Confirm
        btnAddEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUpdateItem();
            }
        });

        // Return to previous
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // place holder
                ItemID = "";
                finish();
            }
        });

        // Handle Image Selection
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // Check Item was selected from the previous activity.
        Intent intent = getIntent();
        ItemID = intent.getStringExtra("InventoryActivity.ItemID");
        Helper.Log("Passed ItemID from InventoryActivity is " + ItemID);
        if (ItemID != null && !ItemID.isEmpty()) {
            try {
                readItem();
            } catch (Exception ex) {
                Helper.Log("Failed to cast.");
            }
        }
    }

    /**
     *  Create
     */
    private void createUpdateItem() {

        // Validate User's input.
        ValidationResult validation_result = validatesSaveItem();

        if (validation_result.hasError()) {
            Helper.ToastNotify(AddItemActivity.this, validation_result.getErrorMessage());
            return;
        }

        // Create New Item
        if (ItemID == null || ItemID.isEmpty()) {

            Helper.Log("Create New Item...");

            Item item = createNewItem();

            db.collection("items")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {

                        Helper.ToastNotify(AddItemActivity.this, "Item saved successfully");

                        Helper.Log("Item Saved...");

                        finish();

                        Helper.Log("Close activity...");
                    })
                    .addOnFailureListener(e -> {
                        Helper.ToastNotify(AddItemActivity.this, "Failed to save item");

                        Helper.Log("Item Not Saved");
                    });
            finish();
        } else {

            Helper.Log("Update Existing Item ID " + ItemID);

            Item item = createNewItem();
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", item.getName());
            updates.put("price", item.getPrice());
            updates.put("quantity", item.getQuantity());
            updates.put("imageBase64", item.getImageBase64());

            Helper.Log("Before Exec");

            db.collection("items").document(ItemID)
                    .set(updates, SetOptions.merge()).addOnSuccessListener(success -> {
                        Helper.ToastNotify(AddItemActivity.this, "Item saved successfully");
                        finish();
                    }).addOnFailureListener(fail -> {
                        Helper.ToastNotify(AddItemActivity.this, "Failed to save item");
                        finish();
                    });

            Helper.Log("After Exec");
            finish();
        }
    }

    // Read Element
    private void readItem() {

        Helper.Log("readItem =>>" + ItemID);

        DocumentReference docRef = db.collection("items").document(ItemID);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            // log(documentSnapshot.toString());

                            String name = documentSnapshot.getString("name");
                            float price = documentSnapshot.get("price", Float.class);
                            int quantity = documentSnapshot.get("quantity", Integer.class);
                            String image64 = documentSnapshot.getString("imageBase64");

                            edItemName.setText(name);
                            edItemPrice.setText(price + "");
                            edItemQty.setText(quantity + "");

                            if (image64 != null && image64.length() > 0) {
                                byte[] byteArray = android.util.Base64.decode(image64, android.util.Base64.DEFAULT);
                                Bitmap bmp = Helper.getBitmapFromBytes(byteArray);
                                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false));
                            }

                        } else {
                            Helper.Log("No such document");                        }
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Helper.Log("Error getting document");
                    }
                });
    }

    private Item createNewItem() {
        // log("before");
        String name = edItemName.getText().toString().trim();
        int qty = Integer.parseInt(edItemQty.getText().toString().trim());
        float price = Float.parseFloat(edItemPrice.getText().toString().trim());

        if (ItemID == null || ItemID.isEmpty()) {
            ItemID = UUID.randomUUID().toString();
            Helper.Log("Set Item ID => " + ItemID);
        }

        String base64 = "";

        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();
        byte[] imageBytes = Helper.getBytesFromBitmap(bitmap);

        if (imageBytes.length > 0) {
            base64 = Base64.getEncoder().encodeToString(imageBytes);
        }

        Item itemToReturn = new Item(ItemID, name, qty, price, base64);
        Helper.Log("Item Created " + itemToReturn);

        return itemToReturn;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Validate given data before saving Item
     */
    private ValidationResult validatesSaveItem() {
        ValidationResult validationResult;
        if (edItemName.getText().toString().isBlank() || edItemName.getText().toString().isEmpty()) {
            validationResult = new ValidationResult(true, "Name can't be empty.");
            return validationResult;
        }

        if (edItemPrice.getText().toString().isBlank() || edItemPrice.getText().toString().isEmpty()) {
            validationResult = new ValidationResult(true, "Price can't be empty.");
            return validationResult;
        }

        if (Float.parseFloat(edItemPrice.getText().toString()) <= 0) {
            validationResult = new ValidationResult(true, "Price can't be negative or zero.");
            return validationResult;
        }

        if (edItemQty.getText().toString().isBlank() || edItemQty.getText().toString().isEmpty()) {
            validationResult = new ValidationResult(true, "Qty can't be empty.");
            return validationResult;
        }

        if (edItemQty.getText() != null) {
            int qty = Integer.parseInt(edItemQty.getText().toString().trim());

            if (qty <= 0) {
                validationResult = new ValidationResult(true, "Qty can't be negative or zero.");
                return validationResult;
            }
        }

        validationResult = new ValidationResult(false, "");
        return validationResult;
    }

    /**
     * Show UI Dialog to Pick the image from the galary.
     */
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }




    // Bind UI elements with UI Classes.
    private void initViews() {
        // bind item details from UI
        imageView = findViewById(R.id.imgViewItem);
        edItemName = findViewById(R.id.edItemName);
        edItemPrice = findViewById(R.id.edItemPrice);
        edItemQty = findViewById(R.id.edItemQty);

        btnAddEdit = findViewById(R.id.btnAddItem);
        btnCancel = findViewById(R.id.btnCancel);
    }
}