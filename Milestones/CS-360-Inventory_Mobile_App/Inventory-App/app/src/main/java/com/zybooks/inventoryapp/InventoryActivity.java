package com.zybooks.inventoryapp;

import android.Manifest;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.InventoryItem;
import com.zybooks.inventoryapp.repo.InventoryAdapter;
import com.zybooks.inventoryapp.repo.InventoryDatabase;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity
implements
InventoryAdapter.OnDeleteItemButtonClickListener,
InventoryAdapter.OnItemClickListener
{
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;
    private GridView gridView;
    private FloatingActionButton btnSendSms,btnAddItem;
    private ArrayList<InventoryItem> inventoryItems;
    private  InventoryDatabase inventoryDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        refreshData();
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("SMS Permission Needed")
                .setMessage(R.string.notification_message)
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(InventoryActivity.this,
                                new String[]{android.Manifest.permission.SEND_SMS},
                                SMS_PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Helper.ToastNotify(InventoryActivity.this,"Permission denied. Some features may not work.");
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void checkAndRequestSmsPermission() {
        int selfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (selfPermission != PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog();
        } else {
            // Permission has already been granted
            sendSms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending SMS
                Toast.makeText(this,
                        "Permission granted.",
                        Toast.LENGTH_SHORT).show();
                sendSms();
            } else {
                // Permission denied
                Toast.makeText(this,
                        "Permission denied. Cannot send SMS.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSms(){
        String phoneNumber = "929-262-8798";
        String message = "Item# IPhone13 Qty is zero";

        // Create an intent to send SMS
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
        smsIntent.putExtra("sms_body", message);

        // Start the SMS app
        startActivity(smsIntent);
    }

    @Override
    public void onItemClick(int position) {
        InventoryItem item = inventoryItems.get(position);
        Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
        intent.putExtra("InventoryActivity.ItemID",String.valueOf(item.getId()));
        startActivity(intent);
    }

    @Override
    public void onItemDeleteButtonClick(int position) {
        InventoryItem item = inventoryItems.get(position);
        new AlertDialog.Builder(InventoryActivity.this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean res = inventoryDatabase.deleteItemById(item.getId());
                    if(res){
                        refreshData();
                    }else{
                        Helper.ToastNotify(InventoryActivity.this,"Failed to delete Item.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    void refreshData(){
        inventoryDatabase = new InventoryDatabase(this);
        // Get mock data
        inventoryItems = inventoryDatabase.getAllItems();
        // MockInventoryData.generateInventoryItems();

        // Set up GridView with adapter
        gridView = findViewById(R.id.gridView);
        InventoryAdapter adapter = new InventoryAdapter(this, inventoryItems,this,this);
        gridView.setAdapter(adapter);

        // Add New Item
        btnAddItem = findViewById(R.id.addButton);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start AddItemActivity
                Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        // Send SMS
        btnSendSms = findViewById(R.id.btnSendSms);
        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestSmsPermission();
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        refreshData();
    }
}