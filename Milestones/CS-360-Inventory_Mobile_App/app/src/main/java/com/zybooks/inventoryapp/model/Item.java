package com.zybooks.inventoryapp.model;

public class Item {
    private int id=-1;
    private String name="";
    private int quantity=0;
    private float price=0.0f;
    private byte[] imageData;
    public Item(){

    }
    public Item(int id, String name, int quantity, float price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
    public Item(int id, String name, int quantity, float price, byte[] imageData) {
        this(id,name,quantity,price);
        setImage(imageData);
    }

    private void setImage(byte[] imageData)
    {
        this.imageData = imageData;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public float getPrice(){
        return  price;
    }
    public byte[] getImage(){return imageData;}

}
