package com.zybooks.inventoryapp.repo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zybooks.inventoryapp.R;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.Item;

import java.util.ArrayList;

///
/// ItemsAdapter is the intermediate layer between the database and the UI layer.
///
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.InventoryViewHolder> {
    private final Context context;
    // Custom interfaces for Users interactions
    private final OnDeleteItemButtonClickListener deleteButtonClickListener;
    private final OnItemClickListener itemClickListener;
    // A container for Loaded Items
    private ArrayList<Item> items;

    // Constructor
    public ItemsAdapter(Context context,
                        ArrayList<Item> items,
                        OnDeleteItemButtonClickListener deleteListener,
                        OnItemClickListener itemListener) {
        this.context = context;
        this.items = items;

        // Bind the implementation with the designated actions.
        this.deleteButtonClickListener = deleteListener;
        this.itemClickListener = itemListener;
    }


    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Item item = items.get(position);
        Log.wtf("MOE", "onBindViewHolder => " + item.toString());

        // Bind Items details to ViewHolder
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.format("Price: %s", item.getPrice()));
        holder.quantityTextView.setText(String.format("Qty: %d", item.getQuantity()));
        holder.tvItemID.setText(item.getId());

        // render item Image
        if (item.getImageBase64() != null && !item.getImageBase64().isEmpty()) {
            byte[] bytes = Base64.decode(item.getImageBase64(), Base64.DEFAULT);
            Bitmap bmp = Helper.getBitmapFromBytes(bytes);
            holder.imgView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false));
        } else {
            holder.imgView.setImageDrawable(null); // Clear image if no data
        }

        // bind the delete action
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteButtonClickListener != null) {
                deleteButtonClickListener.onItemDeleteButtonClick(position);
            }
        });

        // bind the view action
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Handler for Item Delete.
    public interface OnDeleteItemButtonClickListener {
        void onItemDeleteButtonClick(int position);
    }

    // Handler for Item Click
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Parse and Link the Item data to UI elements.
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, quantityTextView, priceTextView, tvItemID;
        ImageView imgView;
        ImageButton btnDelete;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind the UI Views with UI instances..
            nameTextView = itemView.findViewById(R.id.item_name);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            priceTextView = itemView.findViewById(R.id.item_price);
            tvItemID = itemView.findViewById(R.id.tvItemID);
            imgView = itemView.findViewById(R.id.item_image);

            btnDelete = itemView.findViewById(R.id.btnDeleteItem);
        }
    }
}
