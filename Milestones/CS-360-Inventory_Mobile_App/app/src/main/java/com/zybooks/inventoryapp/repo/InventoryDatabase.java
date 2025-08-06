package com.zybooks.inventoryapp.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zybooks.inventoryapp.abstraction.IItemDbTransactions;
import com.zybooks.inventoryapp.abstraction.IUsersDbTransactions;
import com.zybooks.inventoryapp.model.Item;
import com.zybooks.inventoryapp.model.User;

import java.util.ArrayList;
import java.util.List;


public class InventoryDatabase extends SQLiteOpenHelper implements IUsersDbTransactions,
        IItemDbTransactions {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int VERSION = 1;
    public InventoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // Inventory Table
    private static final class InventoryTable {
        private static final String TABLE = "items";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_QTY = "qty";
        private static final String COL_PRICE = "price";
        private static final String COl_IMAGE = "image";
    }

    // Users Table
    private static final class UsersTable {
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    // OnCreation
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + InventoryTable.TABLE + " (" +
                InventoryTable.COL_ID + " integer primary key autoincrement, " +
                InventoryTable.COL_NAME + " text, " +
                InventoryTable.COL_QTY + " int, " +
                InventoryTable.COL_PRICE + " float, "+
                InventoryTable.COl_IMAGE +" BLOB)");

        db.execSQL("create table " + UsersTable.TABLE + " (" +
                UsersTable.COL_ID + " integer primary key autoincrement, " +
                UsersTable.COL_USERNAME + " text, " +
                UsersTable.COL_PASSWORD + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + InventoryTable.TABLE);
        db.execSQL("drop table if exists " + UsersTable.TABLE);

        onCreate(db);
    }

    private ContentValues upsert(int Id,String name, byte[] image, int qty, float price){
        ContentValues contentValues = new ContentValues();

        // push the data values...
        if(Id > 0){
            contentValues.put(InventoryTable.COL_ID,Id);
        }

        contentValues.put(InventoryTable.COL_NAME, name);
        contentValues.put(InventoryTable.COL_QTY,qty );
        contentValues.put(InventoryTable.COL_PRICE, price);
        contentValues.put(InventoryTable.COl_IMAGE,image);

        return contentValues;
    }


    // Get all Items from database
    public ArrayList<Item> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM " + InventoryTable.TABLE, null);
        ArrayList<Item> list = new ArrayList<>();
       /*
        if (cursor != null){
            if (cursor.moveToFirst()) {
                do {

                    Item item
                            = new Item(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getFloat(3),
                            cursor.getBlob(4));
                    list.add(item);
                } while (cursor.moveToNext());
            }

        }
        if(cursor != null)
            cursor.close();
        db.close();
        */
        return list;
    }

    /**
     * Get User By UserName
     * */
    public User getUserByUserName(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                UsersTable.COL_ID,
                UsersTable.COL_USERNAME,
                UsersTable.COL_PASSWORD
        };

        User usr = new User();
        String selection = UsersTable.COL_USERNAME + " = ?";
        String[] selectionArgs = {username };
        Cursor cursor =  db.query(UsersTable.TABLE,projection,selection,selectionArgs,null,null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                usr = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2));
                cursor.close();
                db.close();
                return usr;
            }
        }
        db.close();
        return usr;
    }

    /**
     * Check if Given username is already used before
     * */
    public boolean isUsernameUsed(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                UsersTable.COL_USERNAME,
                UsersTable.COL_PASSWORD
        };

        User usr = new User();
        String selection = UsersTable.COL_USERNAME + " = ?";
        String[] selectionArgs = {username };
        Cursor cursor =  db.query(UsersTable.TABLE,projection,selection,selectionArgs,null,null, null);

        if(cursor != null && cursor.getCount()>0){
            cursor.close();
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    /**
     * Get Item by ID
     * */
    public Item getItemById(int productId){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                InventoryTable.COL_ID,
                InventoryTable.COL_NAME,
                InventoryTable.COL_QTY,
                InventoryTable.COL_PRICE,
                InventoryTable.COl_IMAGE
        };

        String selection = InventoryTable.COL_ID + " = ?";
        String[] selectionArgs = {Integer.toString(productId) };
        Cursor cur =  db.query(InventoryTable.TABLE,projection,selection,selectionArgs,null,null, null);
        ArrayList<Item> list = new ArrayList<>();
        Item item = new Item();
        /*
        if(cur != null && cur.getCount()>0){
            if (cur.moveToFirst()) {
                item   = new Item(cur.getInt(0),
                                        cur.getString(1),
                                        cur.getInt(2),
                                        cur.getFloat(3),
                                        cur.getBlob(4));
            }
        }

        if(cur != null)
            cur.close();
        db.close();
        */
        return item;
    }

   // Create New Item
    public boolean insertItem(String name, byte[] image, int qty, float price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = upsert(-1,name,image,qty,price);
        long result = db.insert(InventoryTable.TABLE, null, contentValues);
        db.close();
        return result != -1;
    }

    // Update Existing Item
    public boolean editItem(int id, String name, byte[] image, int qty, float price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = upsert(id,name,image,qty,price);
        String selection = InventoryTable.COL_ID + " = ?";
        String[] selectionArgs = {Integer.toString(id) };
        long result = db.update(InventoryTable.TABLE, contentValues,selection,selectionArgs);
        db.close();
        return result != -1;

    }

    /**
     * Delete Item using Item ID
     * */
    @Override
    public boolean deleteItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = InventoryTable.COL_ID + " = ?";
        String[] selectionArgs = {Integer.toString(id) };
        int res = db.delete(InventoryTable.TABLE,selection,selectionArgs);
        db.close();
        return res > 0;
    }


    /**
     * Create New User
     * */
    public boolean createUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // push the data values...
        contentValues.put(UsersTable.COL_PASSWORD, password);
        contentValues.put(UsersTable.COL_USERNAME, username);

        long result = db.insert(UsersTable.TABLE, null, contentValues);
        db.close();
        return result != -1;

    }

    /**
     * Get All users
     */
    public List<User> getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM " + UsersTable.TABLE, null);
        ArrayList<User> list = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            if (cursor.moveToFirst()) {
                do {
                    User item
                            = new User(
                            cursor.getString(1),
                            cursor.getString(2));
                    list.add(item);
                } while (cursor.moveToNext());
            }
        }

        if(cursor != null)
            cursor.close();
        db.close();
        return list;
    }
}