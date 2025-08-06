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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.Item;
import com.zybooks.inventoryapp.model.ValidationResult;
import com.zybooks.inventoryapp.repo.InventoryDatabase;

import java.io.IOException;

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private EditText edItemName,edItemQty,edItemPrice;
    private InventoryDatabase inventoryDatabase;
    private Button btnAddEdit,btnCancel;
    private ValidationResult validationResult;
    private int ItemID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        inventoryDatabase = new InventoryDatabase(this);
        // bind item details from UI
        imageView  = findViewById(R.id.imgViewItem);
        edItemName = findViewById(R.id.edItemName);
        edItemPrice = findViewById(R.id.edItemPrice);
        edItemQty = findViewById(R.id.edItemQty);

        btnAddEdit = findViewById(R.id.btnAddItem);

        btnAddEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUpdateItem();
                finish();
            }
        });
        btnCancel = findViewById(R.id.btnCancel);
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

        if(strItemID != null && strItemID.length() >0){
            try {
                Log.w("EXC",strItemID);
                ItemID = Integer.parseInt(strItemID);
                if(ItemID > 0){
                    readItem();
                }
            }catch (Exception ex){
                Log.w("EXC","Failed to cast.");
            }

        }

    }

    void createUpdateItem(){
        String name = edItemName.getText().toString();
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();
        byte[] imageBytes = Helper.getBytesFromBitmap(bitmap);
        int qty =  Integer.parseInt( edItemQty.getText().toString());
        float price = Float.parseFloat(edItemPrice.getText().toString());
        boolean result;

        if(ItemID == -1){
            result  = inventoryDatabase.insertItem(name,imageBytes,qty,price);
        }
        else{
            result  = inventoryDatabase.editItem(ItemID,name,imageBytes,qty,price);
        }

        if(result){
            Helper.ToastNotify(AddItemActivity.this,"Item Created/Updated Successfully.");
        }
        else {
            Helper.ToastNotify(AddItemActivity.this,"Failed to save/update item.");
        }
    }

    /**
     * Validate given data before saving Item
     * */
    ValidationResult validatesSaveItem(){
        if(edItemName.getText().toString().isBlank() || edItemName.getText().toString().isEmpty()){
            validationResult = new ValidationResult(true,"Name can't be empty");
            return validationResult;
        }

        if(edItemPrice.getText().toString().isBlank() || edItemPrice.getText().toString().isEmpty()){
            validationResult = new ValidationResult(true,"Price can't be empty");
            return validationResult;
        }

        if( Float.valueOf (edItemPrice.getText().toString()) <= 0){
            validationResult = new ValidationResult(true,"Price can't be negative or zero");
            return validationResult;
        }

        if(edItemQty.getText().toString().isBlank() || edItemQty.getText().toString().isEmpty()){
            validationResult = new ValidationResult(true,"Qty can't be empty");
            return validationResult;
        }

        if( Integer.getInteger(edItemQty.getText().toString()) <= 0){
            validationResult = new ValidationResult(true,"Qty can't be negative or zero");
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

    void readItem(){
        Item item =  inventoryDatabase.getItemById(ItemID);

        edItemName.setText(item.getName());
        edItemPrice.setText(String.valueOf(item.getPrice()));
        edItemQty.setText(String.valueOf(item.getQuantity()));
        byte[] bitarray = item.getImage();

        if  (bitarray != null && bitarray.length > 0){
            // bytes
            Bitmap bmp = Helper.getBitmapFromBytes(bitarray);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false));
        }
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
}