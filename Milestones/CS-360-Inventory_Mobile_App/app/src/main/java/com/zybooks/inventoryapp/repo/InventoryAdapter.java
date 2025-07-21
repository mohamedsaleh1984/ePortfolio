package com.zybooks.inventoryapp.repo;
import static android.widget.Toast.LENGTH_LONG;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zybooks.inventoryapp.InventoryActivity;
import com.zybooks.inventoryapp.R;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class InventoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<InventoryItem> inventoryItems;
    private  TextView nameTextView, quantityTextView,priceTextView,tvItemID;
    private ImageView imgView;
    private ImageButton btnDelete;
    private OnDeleteItemButtonClickListener deleteButtonClickListener;
    private OnItemClickListener itemClickListener;
    public interface OnDeleteItemButtonClickListener {
        void onItemDeleteButtonClick(int position);
    }
    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public InventoryAdapter(Context context,
                            ArrayList<InventoryItem> inventoryItems,
                            OnDeleteItemButtonClickListener onDeletelistener,
                            OnItemClickListener onItemlistener) {
        this.context = context;
        this.inventoryItems = inventoryItems;
        this.deleteButtonClickListener = onDeletelistener;
        this.itemClickListener = onItemlistener;
    }

    @Override
    public int getCount() {
        return inventoryItems.size();
    }

    @Override
    public Object getItem(int position) {
        return inventoryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_layout, parent, false);
        }

        // Get Selected It DTO
        InventoryItem item = inventoryItems.get(position);
        // Get items UI representation
         nameTextView = convertView.findViewById(R.id.item_name);
         quantityTextView = convertView.findViewById(R.id.item_quantity);
         priceTextView = convertView.findViewById(R.id.item_price);
         tvItemID = convertView.findViewById(R.id.tvItemID);
         imgView  = convertView.findViewById(R.id.item_image);
         btnDelete = convertView.findViewById(R.id.btnDeleteItem);

        // set UI elements value
        nameTextView.setText(item.getName());
        priceTextView.setText("Price: "+item.getPrice());
        quantityTextView.setText("Qty: " + item.getQuantity());
        tvItemID.setText(String.valueOf( item.getId()));
        byte[] bitarray = item.getImage();

        if  (bitarray != null && bitarray.length > 0){
            // Convert Image bytes to Bitmap and render it
            Bitmap bmp = Helper.getBitmapFromBytes(bitarray);
            imgView = convertView.findViewById(R.id.item_image);
            imgView.setImageBitmap(Bitmap.createScaledBitmap(bmp,
                                    bmp.getWidth(),
                                    bmp.getHeight(),
                                false));
        }

        btnDelete.setOnClickListener(v -> {
            if (deleteButtonClickListener != null) {
                deleteButtonClickListener.onItemDeleteButtonClick(position);
            }
        });

        if(itemClickListener != null){
            convertView.setOnClickListener(v -> {
                if(itemClickListener != null){
                    itemClickListener.onItemClick(position);
                }
            });
        }

        return convertView;
    }
}
