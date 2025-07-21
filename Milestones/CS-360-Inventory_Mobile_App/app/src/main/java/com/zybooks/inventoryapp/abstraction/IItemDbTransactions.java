package com.zybooks.inventoryapp.abstraction;

import com.zybooks.inventoryapp.model.InventoryItem;
import com.zybooks.inventoryapp.model.UserDto;

import java.util.ArrayList;
import java.util.List;

public interface IItemDbTransactions {
    ArrayList<InventoryItem> getAllItems() ;
    InventoryItem getItemById(int productId);
    boolean insertItem(String name, byte[] image, int qty, float price);
    boolean editItem(int id, String name, byte[] image, int qty, float price);
    boolean deleteItemById(int id);

}
