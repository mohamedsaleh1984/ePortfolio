package com.zybooks.inventoryapp.repo;

import android.content.Context;
import android.graphics.Bitmap;
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

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.InventoryViewHolder> {

    private final Context context;
    private   ArrayList<Item> items;
    private final OnDeleteItemButtonClickListener deleteButtonClickListener;
    private final OnItemClickListener itemClickListener;

    public interface OnDeleteItemButtonClickListener {
        void onItemDeleteButtonClick(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ItemsAdapter(Context context,
                            ArrayList<Item> items,
                            OnDeleteItemButtonClickListener deleteListener,
                            OnItemClickListener itemListener) {
        this.context = context;
        this.items = items;
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

        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText("Price: " + item.getPrice());
        holder.quantityTextView.setText("Qty: " + item.getQuantity());
        holder.tvItemID.setText(String.valueOf(item.getId()));

        String imageUrl  = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            /*
            Bitmap bmp = Helper.getBitmapFromBytes(bitarray);
            holder.imgView.setImageBitmap(Bitmap.createScaledBitmap(
                    bmp, bmp.getWidth(), bmp.getHeight(), false));
            */
        } else {
            holder.imgView.setImageDrawable(null); // Clear image if no data
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteButtonClickListener != null) {
                deleteButtonClickListener.onItemDeleteButtonClick(position);
            }
        });

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

    public void setFilteredList(ArrayList<Item> items){
        this.items = items;
    }
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, quantityTextView, priceTextView, tvItemID;
        ImageView imgView;
        ImageButton btnDelete;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            priceTextView = itemView.findViewById(R.id.item_price);
            tvItemID = itemView.findViewById(R.id.tvItemID);
            imgView = itemView.findViewById(R.id.item_image);
            btnDelete = itemView.findViewById(R.id.btnDeleteItem);
        }
    }
}
