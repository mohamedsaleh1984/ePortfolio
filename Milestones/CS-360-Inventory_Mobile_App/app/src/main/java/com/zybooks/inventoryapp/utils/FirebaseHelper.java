package com.zybooks.inventoryapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final StorageReference storageRef;

    private FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseFirestore getFirestore() {
        return db;
    }

    public StorageReference getStorageReference() {
        return storageRef;
    }

    public String getCurrentUserId() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }
}
