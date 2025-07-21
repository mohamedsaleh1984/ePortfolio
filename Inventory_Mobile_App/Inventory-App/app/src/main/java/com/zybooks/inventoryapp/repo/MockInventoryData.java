package com.zybooks.inventoryapp.repo;

import com.zybooks.inventoryapp.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class MockInventoryData {
    /**
     * Generate Random Items
     */
    public static List<InventoryItem> generateInventoryItems() {
        List<InventoryItem> items = new ArrayList<>();
        int mockRandom = (int)(Math.random() * 20)+5;
        int idCounter = 1;

        for (int i = 0; i < mockRandom; i++) {

            items.add(new InventoryItem(
                         idCounter++,
                        "NAME-"+ idCounter,
                        (int) (Math.random() * 50) + 1,
                        (int) (Math.random() * 100) + 1)
                );

        }

        return items;
    }
}
