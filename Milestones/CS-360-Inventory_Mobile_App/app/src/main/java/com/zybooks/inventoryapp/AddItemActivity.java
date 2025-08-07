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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.Help;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.Item;
import com.zybooks.inventoryapp.model.ValidationResult;
import com.zybooks.inventoryapp.utils.FirebaseHelper;

import java.io.IOException;
import java.util.UUID;

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final  String TAG="MOE";
    private ImageView imageView;
    private EditText edItemName,edItemQty,edItemPrice;
    private Button btnAddEdit,btnCancel;
    private ValidationResult validationResult;
    private String ItemID = "",ImageUrl="";
    private Uri filePath= null;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private void createUpdateItem(){

        ValidationResult validation_result = validatesSaveItem();
        if(validation_result.hasError()){
            Helper.ToastNotify(AddItemActivity.this,validation_result.getErrorMessage());
            return;
        }

        if(ItemID.isEmpty()){

           // uploadImageToFirebaseStorage(imageUri, ItemID);

            Item item =createNewItem();

            db.collection("items")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        Log.w(TAG,"ITEM SAVED....");
                        //Helper.ToastNotify(AddItemActivity.this, "Item saved successfully");

                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Helper.ToastNotify(AddItemActivity.this, "Failed to save item");
                        Log.w(TAG,"ITEM NOT SAVED....");
                    });

        }
        else{
            //TODO: Update Item
        }


    }



    private void readItem(){

        DocumentReference docRef =  db.collection("items").document(ItemID);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            // Log.wtf(TAG,documentSnapshot.toString());

                            String id = documentSnapshot.getString("id");
                            String name = documentSnapshot.getString("name");
                            float price = documentSnapshot.get("price", Float.class);
                            int quantity = documentSnapshot.get("quantity",Integer.class);
                            String imageUrl = documentSnapshot.getString("imageUrl");


                            ItemID = id;
                            edItemName.setText(name);
                            edItemPrice.setText(price+"");
                            edItemQty.setText(quantity+"");

                            String itemImageUrl = documentSnapshot.getString("imageUrl");
                            if  (itemImageUrl != null && !itemImageUrl.isEmpty() ){
                                loadImageFromFirebase(itemImageUrl,imageView);
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

    private Item createNewItem(){
        String name = edItemName.getText().toString().trim();
        int qty =  Integer.parseInt( edItemQty.getText().toString().trim());
        float price = Float.parseFloat(edItemPrice.getText().toString().trim());
        String ItemID = UUID.randomUUID().toString();

        return  new Item(ItemID,name,qty,price,ImageUrl);
    }

    private void uploadImageToFirebaseStorage( String ImageId) {
        if(filePath != null){
            // Get a reference to Firebase Storage
            storageRef = FirebaseHelper.getInstance().getStorageReference();
            // Create a unique image name
            String imagePath = "images/" + ImageId + ".jpg";
            // Create a reference to the file you want to upload
            StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID().toString());

            imagesRef.putFile(filePath)
                    .addOnSuccessListener(success->{
                        imagesRef.getDownloadUrl().addOnSuccessListener(uri ->{
                            ImageUrl= uri.toString();
                            Toast.makeText(AddItemActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(fail->{
                        Toast.makeText(AddItemActivity.this, "Upload failed: " + fail.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
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
        /*
        if(imageUri == null){
            validationResult = new ValidationResult(true,"You need to pick product image.");
            return validationResult;
        }
        */

        validationResult = new ValidationResult(false,"");
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
        storageRef = FirebaseHelper.getInstance().getStorageReference();

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
        ItemID = intent.getStringExtra("InventoryActivity.ItemID");

        if(ItemID != null && !ItemID.isEmpty()){
            try {
                readItem();
            }catch (Exception ex){
                Log.w("EXC","Failed to cast.");
            }
        }
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


}