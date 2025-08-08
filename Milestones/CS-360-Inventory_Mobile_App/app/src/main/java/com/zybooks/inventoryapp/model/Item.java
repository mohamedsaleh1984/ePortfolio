package com.zybooks.inventoryapp.model;

import androidx.annotation.NonNull;

import java.util.UUID;

//TODO: Add Comments.
public class Item {
    private String id = "";
    private String name = "";
    private int quantity = 0;
    private float price = 0.0f;
    private String imageBase64;
    private long createdAt;

    public Item() {
    }

    public Item(String id, String name, int quantity, float price, String imageBase64) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageBase64 = imageBase64;
        this.createdAt = System.currentTimeMillis();
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImage(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "Item [id=" + this.id + ", name=" + name + ", price=" + price + ", qty=" + quantity +"]";
    }
    public String toStringWithImage() {
        return "Item [id=" + this.id + ", name=" + name + ", price=" + price + ", qty=" + quantity + " , image64=" + imageBase64 + "]";
    }
}
