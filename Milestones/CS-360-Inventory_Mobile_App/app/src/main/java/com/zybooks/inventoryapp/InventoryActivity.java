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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.zybooks.inventoryapp.utils.Helper;
import com.zybooks.inventoryapp.model.Item;
import com.zybooks.inventoryapp.repo.ItemsAdapter;
import com.zybooks.inventoryapp.utils.FirebaseHelper;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity
        implements
        ItemsAdapter.OnDeleteItemButtonClickListener,
        ItemsAdapter.OnItemClickListener {
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;
    private ArrayList<Item> itemList;
    private SearchView searchView;
    private ItemsAdapter itemsAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FloatingActionButton btnSendSms, btnAddItem;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        initViews();

        // Bind ArrayList with Adapter.
        itemList = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(this, itemList, this, this);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemsAdapter);

        db = FirebaseHelper.getInstance().getFirestore();

        // Search Item handler
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

        // Load Items
        loadItems();

        //
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start AddItemActivity
                Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestSmsPermission();
            }
        });


    }

    // Load Items from Firebase
    void loadItems() {
        db.collection("items")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(InventoryActivity.this, "Error loading items", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    itemList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Item item = doc.toObject(Item.class);
                        item.setId(doc.getId());
                        itemList.add(item);
                    }
                    itemsAdapter.notifyDataSetChanged();
                });
    }

    // Bind UI with Component.
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnSendSms = findViewById(R.id.btnSendSms);
        btnAddItem = findViewById(R.id.addButton);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
    }

    // Filter Items
    private void filterList(String text) {
        ArrayList<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_LONG).show();
        } else {
            itemsAdapter.setFilteredList(filteredList);
        }
    }

    // Show Permission Dialog
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
                        Helper.ToastNotify(InventoryActivity.this, "Permission denied. Some features may not work.");
                    }
                })
                .setCancelable(false)
                .show();
    }


    // Check App Send SMS Permission
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

    // Send SMS
    private void sendSms() {
        String phoneNumber = "929-262-8798";
        String message = "Item# IPhone13 Qty is zero";

        // Create an intent to send SMS
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
        smsIntent.putExtra("sms_body", message);

        // Start the SMS app
        startActivity(smsIntent);
    }

    // On Click Add New Item
    @Override
    public void onItemClick(int position) {
        Item item = itemList.get(position);
        Helper.Log(item.toString());


        Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
        intent.putExtra("InventoryActivity.ItemID", item.getId());
        Helper.Log(item.toString());
        startActivity(intent);
    }

    // On Click Delete item.

    @Override
    public void onItemDeleteButtonClick(int position) {
        Item item = itemList.get(position);
        new AlertDialog.Builder(InventoryActivity.this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete \"" + item.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem(item, position))
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Delete Item from firebase.
    private void deleteItem(Item item, int position) {
        db.collection("items").document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove item from local list and notify adapter
                    itemList.remove(position);
                    itemsAdapter.notifyItemRemoved(position);
                    itemsAdapter.notifyItemRangeChanged(position, itemList.size());
                    Toast.makeText(InventoryActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(InventoryActivity.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }
}