package com.zybooks.inventoryapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.InventoryItem;

import com.zybooks.inventoryapp.repo.InventoryDatabase;
import com.zybooks.inventoryapp.repo.ItemsAdapter;
import com.zybooks.inventoryapp.repo.MockInventoryData;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity
implements
        ItemsAdapter.OnDeleteItemButtonClickListener,
        ItemsAdapter.OnItemClickListener
{
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;
    private ArrayList<InventoryItem> itemsList;
    private  InventoryDatabase inventoryDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });


        refreshData();
    }

    void refreshData(){
        inventoryDatabase = new InventoryDatabase(this);

        itemsList = inventoryDatabase.getAllItems();
        // Get mock data
        //itemsList = MockInventoryData.generateInventoryItems();

        Log.w("TEST","COUNT IS " +itemsList.size());


        ItemsAdapter itemsAdapter = new ItemsAdapter(this, itemsList, this, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemsAdapter);

        // Add New Item
        FloatingActionButton btnAddItem = findViewById(R.id.addButton);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start AddItemActivity
                Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        // Send SMS
        FloatingActionButton btnSendSms = findViewById(R.id.btnSendSms);
        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestSmsPermission();
            }
        });
    }

    private void filterList(String text){
        List<InventoryItem>  filteredList = new ArrayList<>();
        for(InventoryItem item:itemsList){
            if(item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this,"No data found",Toast.LENGTH_LONG).show();
        }else{

        }
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
        InventoryItem item = itemsList.get(position);
        Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
        intent.putExtra("InventoryActivity.ItemID",String.valueOf(item.getId()));
        startActivity(intent);
    }

    @Override
    public void onItemDeleteButtonClick(int position) {
        InventoryItem item = itemsList.get(position);
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


    @Override
    protected void onResume(){
        super.onResume();
        refreshData();
    }
}