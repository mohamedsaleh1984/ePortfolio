package com.zybooks.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.ValidationResult;
import com.zybooks.inventoryapp.utils.FirebaseHelper;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin, btnRegister;
    private EditText etEmail, etPassword;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseHelper.getInstance().getAuth();

        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, InventoryActivity.class));
            finish();
        }

        initViews();

        btnLogin.setOnClickListener(v -> {

            ValidationResult valid = IsValidToLogin();

            if (!valid.hasError()) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, InventoryActivity.class));
                                finish();
                            } else {
                                Helper.SnackbarNotify(v, "Login failed: You need to register your account first.");
                            }
                        });


            } else {
                Helper.SnackbarNotify(v, valid.getErrorMessage());
            }
        });

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            // Create an intent to start SecondActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);  // Start the second activity
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.edEmailAddress);
        etPassword = findViewById(R.id.edPwd);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private ValidationResult IsValidToLogin() {
        ValidationResult vr = new ValidationResult(false, "");

        if (etEmail.getText().length() == 0) {
            vr.setHasError(true);
            vr.setErrorMessage("You must enter your email.");
            return vr;
        }

        if (etPassword.getText().toString().isEmpty()) {
            vr.setHasError(true);
            vr.setErrorMessage("Password can't be empty.");
            return vr;
        }

        return vr;
    }
}