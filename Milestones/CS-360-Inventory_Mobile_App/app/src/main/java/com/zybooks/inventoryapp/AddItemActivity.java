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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.Item;
import com.zybooks.inventoryapp.model.ValidationResult;
import com.zybooks.inventoryapp.utils.FirebaseHelper;

import java.io.IOException;
import java.util.UUID;

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private EditText edItemName,edItemQty,edItemPrice;

    private Button btnAddEdit,btnCancel;
    private ValidationResult validationResult;
    private String ItemID = "";
    private Uri imageUri= null;
    private  String ImageUrl="";
    private FirebaseFirestore db;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        initViews();

        db = FirebaseHelper.getInstance().getFirestore();
        storageRef = FirebaseHelper.getInstance().getStorageReference();

        evtHandlers();
    }
    private void initViews(){
        // bind item details from UI
        imageView  = findViewById(R.id.imgViewItem);
        edItemName = findViewById(R.id.edItemName);
        edItemPrice = findViewById(R.id.edItemPrice);
        edItemQty = findViewById(R.id.edItemQty);

        btnAddEdit = findViewById(R.id.btnAddItem);
        btnCancel = findViewById(R.id.btnCancel);
    }
    private  void evtHandlers(){
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
        String strItemID = intent.getStringExtra("InventoryActivity.ItemID");

        if(strItemID != null && !strItemID.isEmpty()){
            try {
                readItem();
            }catch (Exception ex){
                Log.w("EXC","Failed to cast.");
            }
        }

    }

    private void createUpdateItem(){

        ValidationResult validation_result = validatesSaveItem();
        if(validation_result.hasError()){
            Helper.ToastNotify(AddItemActivity.this,validation_result.getErrorMessage());
            return;
        }

        String name = edItemName.getText().toString().trim();
        int qty =  Integer.parseInt( edItemQty.getText().toString().trim());
        float price = Float.parseFloat(edItemPrice.getText().toString().trim());

        if(ItemID.isEmpty()){
            String ItemID = UUID.randomUUID().toString();
            uploadImageToFirebaseStorage(imageUri, ItemID);

            Item item = new Item(ItemID,name,qty,price,ImageUrl);
            db.collection("items")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        Helper.ToastNotify(AddItemActivity.this, "Item saved successfully");
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Helper.ToastNotify(AddItemActivity.this, "Failed to save item");
                    });
        }
        else{
            //TODO: Update Item
        }


    }

    /**
     * Validate given data before saving Item
     * */
    private ValidationResult validatesSaveItem(){
        if(edItemName.getText().toString().isBlank() || edItemName.getText().toString().isEmpty()){
            validationResult = new ValidationResult(true,"Name can't be empty.");
            return validationResult;
        }

        if(edItemPrice.getText().toString().isBlank() || edItemPrice.getText().toString().isEmpty()){
            validationResult = new ValidationResult(true,"Price can't be empty.");
            return validationResult;
        }

        if( Float.parseFloat(edItemPrice.getText().toString()) <= 0){
            validationResult = new ValidationResult(true,"Price can't be negative or zero.");
            return validationResult;
        }

        if(edItemQty.getText().toString().isBlank() || edItemQty.getText().toString().isEmpty()){
            validationResult = new ValidationResult(true,"Qty can't be empty.");
            return validationResult;
        }

        if( edItemQty.getText() != null){
            int qty = Integer.parseInt(edItemQty.getText().toString().trim());

            if(qty <= 0){
                validationResult = new ValidationResult(true,"Qty can't be negative or zero.");
                return validationResult;
            }
        }

        if(imageUri == null){
            validationResult = new ValidationResult(true,"You need to pick product image.");
            return validationResult;
        }

        validationResult = new ValidationResult(false,"");
        return validationResult;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void readItem(){
        Item item = new Item();
        edItemName.setText(item.getName());
        edItemPrice.setText(String.valueOf(item.getPrice()));
        edItemQty.setText(String.valueOf(item.getQuantity()));

        String itemImageUrl = item.getImageUrl();

        if  (itemImageUrl != null && !itemImageUrl.isEmpty() ){
            loadImageFromFirebase(itemImageUrl,imageView);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String ImageId) {
        // Get a reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Create a unique image name
        String imagePath = "images/" + ImageId + ".jpg";
        StorageReference imageRef = storageRef.child(ImageUrl);

        // Upload the image
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        ImageUrl = uri.toString();
                        Log.d("Firebase", "Image URL: " + ImageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImageFromFirebase(String imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.item)
                .into(imageView);
    }
}