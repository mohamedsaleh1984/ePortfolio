package com.zybooks.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.UserDto;
import com.zybooks.inventoryapp.model.ValidationResult;
import com.zybooks.inventoryapp.repo.InventoryDatabase;

public class LoginActivity extends AppCompatActivity {
    private Button btnRegister, btnLogin;
    private EditText edUserName,edPwd;
    private InventoryDatabase inventoryDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inventoryDatabase = new InventoryDatabase(this);
        edUserName=findViewById(R.id.edUserName);
        edPwd = findViewById(R.id.edPwd);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start SecondActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);  // Start the second activity
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidationResult valid = IsValidToLogin();
                if(!valid.hasError()){
                    // Create an intent to start InventoryActivity
                    Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
                    startActivity(intent);  // Start the second activity
                }else{
                    Helper.SnackbarNotify(v,valid.getErrorMessage());
                }
            }
        });
    }

    private ValidationResult IsValidToLogin(){
        ValidationResult vr = new ValidationResult(false,"");

        if(edUserName.getText().length() == 0){
            vr.setHasError(true);
            vr.setErrorMessage("You must enter Username.");
            return vr;
        }

        if(edPwd.getText().toString().length() == 0){
            vr.setHasError(true);
            vr.setErrorMessage("Password can't be empty.");
            return vr;
        }

        UserDto res = inventoryDatabase.getUserByUserName(edUserName.getText().toString());
        if(res != null && res.getId()>0){
            if(!res.getPassword().equals( edPwd.getText().toString())){
                vr.setHasError(true);
                vr.setErrorMessage("Please check your account.");
                return vr;
            }
        }

        return vr;
    }
}