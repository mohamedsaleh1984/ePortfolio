package com.zybooks.inventoryapp.model;

import java.util.UUID;

//TODO: Add Comments.
public class Item {
    private String id="";
    private String name="";
    private int quantity=0;
    private float price=0.0f;
    private String imageUrl;
    private long createdAt;
    public Item(){
    }

    public Item( String id, String name, int quantity, float price,String url) {
        this.id=id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = url;
        this.createdAt = System.currentTimeMillis();
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

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
