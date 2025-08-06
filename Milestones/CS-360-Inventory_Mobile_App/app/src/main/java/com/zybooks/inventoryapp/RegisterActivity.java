package com.zybooks.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.Help;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.User;
import com.zybooks.inventoryapp.model.ValidationResult;
import com.zybooks.inventoryapp.repo.InventoryDatabase;
import com.zybooks.inventoryapp.utils.FirebaseHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText edEmailAddress, edPwd1, edPwd2;
    private InventoryDatabase _inventoryDatabase;
    private Button btnRegister,btnCancel;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        mAuth = FirebaseHelper.getInstance().getAuth();
        db = FirebaseHelper.getInstance().getFirestore();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidationResult validationResult = validateRegister();
                if (validationResult.hasError()) {
                    //Helper.ToastNotify(RegisterActivity.this, validationResult.getErrorMessage());
                    Helper.SnackbarNotify(v,validationResult.getErrorMessage());
                } else {
                    RegisterUser(v);
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void RegisterUser(View view){
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        String email = edEmailAddress.getText().toString().trim();
        String password = edPwd1.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(userId, email, password);

                        db.collection("users").document(userId)
                                .set(user)
                                .addOnCompleteListener(userTask -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnRegister.setEnabled(true);

                                    if (userTask.isSuccessful()) {

                                        Helper.SnackbarNotify(view,"Registration successful");
                                        startActivity(new Intent(RegisterActivity.this, InventoryActivity.class));
                                        finish();
                                    } else {

                                        Helper.SnackbarNotify(view, "Failed to save user data");
                                    }
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Helper.SnackbarNotify(view,"Registration failed - " + task.getException().getMessage());
                    }
                });
    }


    void initViews(){
        edEmailAddress = findViewById(R.id.edEmailAddress);
        edPwd1 = findViewById(R.id.edPwd);
        edPwd2 = findViewById(R.id.edReenterPwd);
        btnCancel = findViewById(R.id.btnCancel);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
    }
    ValidationResult validateRegister() {
        ValidationResult vr = new ValidationResult(false, "");

        if (edEmailAddress.getText().toString().isEmpty()) {
            vr.setHasError(true);
            vr.setErrorMessage("Email Address can't be empty");
            return vr;
        }

        if (edEmailAddress.getText().length() > 50) {
            vr.setHasError(true);
            vr.setErrorMessage("Email Address can't length can't be more than 50 characters");
            return vr;
        }

        if (edPwd1.getText().toString().isEmpty()) {
            vr.setHasError(true);
            vr.setErrorMessage("Password can't be empty");
            return vr;
        }

        if (edPwd2.getText().toString().isEmpty()) {
            vr.setHasError(true);
            vr.setErrorMessage("You must re-enter the password");
            return vr;
        }

        if (edPwd2.getText().length()< 6 || edPwd1.getText().length() < 6) {
            vr.setHasError(true);
            vr.setErrorMessage("You must re-enter the password");
            return vr;
        }

        if (!edPwd1.getText().toString().equals(edPwd2.getText().toString())) {
            vr.setHasError(true);
            vr.setErrorMessage("Password are not matching");
            return vr;
        }


        return vr;
    }
}