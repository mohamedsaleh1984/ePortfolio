package com.zybooks.inventoryapp.model;

import java.util.UUID;

//TODO: Add Comments.
public class Item {
    private String id="";
    private String name="";
    private int quantity=0;
    private float price=0.0f;
    private byte[] imageData;
    private long createdAt;
    public Item(){
    }

    public Item( String id, String name, int quantity, float price,byte[]  imageData) {
        this.id=id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageData = imageData;
        this.createdAt = System.currentTimeMillis();
    }

    public  byte[] getImage() { return imageData; }

    public void setImage(byte[] imageBytes) { this.imageData = imageBytes; }

    // Getters
    public String getId() { return id; }

    public String getName() { return name; }

    public int getQuantity() { return quantity; }

    public float getPrice(){
        return  price;
    }

    public long getCreatedAt() { return createdAt; }

    public void setId(String id){
        this.id=id;
    }

    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
