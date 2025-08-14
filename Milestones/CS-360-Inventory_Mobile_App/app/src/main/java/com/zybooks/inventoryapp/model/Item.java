package com.zybooks.inventoryapp.model;

import androidx.annotation.NonNull;

// a Representation for Item Details
public class Item {
    private String id = "";
    private String name = "";
    private int quantity = 0;
    private float price = 0.0f;
    private String imageBase64;
    private long createdAt;
    public Item(String id, String name, int quantity, float price, String imageBase64) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageBase64 = imageBase64;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getImageBase64() {
        return imageBase64;
    }
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
    // Setters
    public void setId(String id) {
        this.id = id;
    }

    // Override the original implementation
    @NonNull
    @Override
    public String toString() {
        return "Item [id=" + this.id + ", name=" + name + ", price=" + price + ", qty=" + quantity +"]";
    }

    public String toStringWithImage() {
        return "Item [id=" + this.id + ", name=" + name + ", price=" + price + ", qty=" + quantity + " , image64=" + imageBase64 + "]";
    }
}
