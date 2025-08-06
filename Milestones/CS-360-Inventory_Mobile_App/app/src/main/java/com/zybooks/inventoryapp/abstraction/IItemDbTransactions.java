package com.zybooks.inventoryapp.abstraction;

import com.zybooks.inventoryapp.model.Item;

import java.util.ArrayList;

public interface IItemDbTransactions {
    ArrayList<Item> getAllItems() ;
    Item getItemById(int productId);
    boolean insertItem(String name, String imageUrl, int qty, float price);
    boolean editItem(String id, String name, String imageUrl, int qty, float price);
    boolean deleteItemById(String id);

}
