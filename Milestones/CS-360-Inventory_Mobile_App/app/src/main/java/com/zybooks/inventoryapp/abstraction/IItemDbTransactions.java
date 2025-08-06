package com.zybooks.inventoryapp.abstraction;

import com.zybooks.inventoryapp.model.Item;

import java.util.ArrayList;

public interface IItemDbTransactions {
    ArrayList<Item> getAllItems() ;
    Item getItemById(int productId);
    boolean insertItem(String name, byte[] image, int qty, float price);
    boolean editItem(int id, String name, byte[] image, int qty, float price);
    boolean deleteItemById(int id);

}
