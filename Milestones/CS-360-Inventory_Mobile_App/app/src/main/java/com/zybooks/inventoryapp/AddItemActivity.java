package com.zybooks.inventoryapp;

import android.content.ClipData;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "MOE";
    private ImageView imageView;
    private EditText edItemName, edItemQty, edItemPrice;
    private Button btnAddEdit, btnCancel;
    private String ItemID = "";
    private FirebaseFirestore db;

    private void createUpdateItem() {

        ValidationResult validation_result = validatesSaveItem();

        if (validation_result.hasError()) {
            Helper.ToastNotify(AddItemActivity.this, validation_result.getErrorMessage());
            return;
        }

        // Create New Item
        if (ItemID == null || ItemID.isEmpty()) {
            log("Create New Item...");

            Item item = createNewItem();

            db.collection("items")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {

                        Helper.ToastNotify(AddItemActivity.this, "Item saved successfully");
                        log("Item Saved...");
                        finish();
                        log("Close activity...");
                    })
                    .addOnFailureListener(e -> {
                        Helper.ToastNotify(AddItemActivity.this, "Failed to save item");
                        log("Item Not Saved");
                    });
            finish();
        } else {
            log("Update Existing Item ID "+ ItemID);
            Item item = createNewItem();
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", item.getName());
            updates.put("price", item.getPrice());
            updates.put("quantity", item.getQuantity());
            updates.put("imageBase64", item.getImageBase64());
            log("Before Exec");
            db.collection("items").document(ItemID)
                    .set(updates, SetOptions.merge()).addOnSuccessListener(success->{
                        Helper.ToastNotify(AddItemActivity.this, "Item saved successfully");
                        finish();
                    }).addOnFailureListener(fail ->{
                        Helper.ToastNotify(AddItemActivity.this, "Failed to save item");
                        finish();
                    });
            log("After Exec");
            finish();
        }
    }

    private void readItem() {
        log("readItem =>>"+ ItemID);
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
                            Log.d(TAG, "No such document");
                        }
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting document", e);
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
            log("Set Item ID => "+ ItemID);
        }

        String base64 = "";

        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();
        byte[] imageBytes = Helper.getBytesFromBitmap(bitmap);

        if(imageBytes.length>0){
            base64 = Base64.getEncoder().encodeToString(imageBytes);
        }

        Item itemToReturn = new Item(ItemID, name, qty, price, base64);
        log("Item Created "+ itemToReturn);

        return itemToReturn;
    }

    private void log(String message) {
        Log.wtf(TAG, message);
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

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        initViews();

        db = FirebaseHelper.getInstance().getFirestore();

        btnAddEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUpdateItem();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // place holder
                ItemID="";
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Intent intent = getIntent();
        ItemID = intent.getStringExtra("InventoryActivity.ItemID");
        log("Passed ItemID from InventoryActivity is " + ItemID);

        if (ItemID != null && !ItemID.isEmpty()) {
            try {
                readItem();
            } catch (Exception ex) {
                Log.w("EXC", "Failed to cast.");
            }
        }
    }

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