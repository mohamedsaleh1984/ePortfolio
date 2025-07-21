package com.zybooks.inventoryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.zybooks.inventoryapp.helper.Helper;
import com.zybooks.inventoryapp.model.ValidationResult;
import com.zybooks.inventoryapp.repo.InventoryDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText edUserName, edPwd1, edPwd2;
    private InventoryDatabase _inventoryDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _inventoryDatabase = new InventoryDatabase(this);
        edUserName = findViewById(R.id.edUserName);
        edPwd1 = findViewById(R.id.edPwd);
        edPwd2 = findViewById(R.id.edReenterPwd);


        Button btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidationResult validationResult = validateCreate();
                if (validationResult.hasError()) {
                    //Helper.ToastNotify(RegisterActivity.this, validationResult.getErrorMessage());
                    Helper.SnackbarNotify(v,validationResult.getErrorMessage());
                } else {
                    boolean res = _inventoryDatabase.createUser(edUserName.getText().toString(), edPwd1.getText().toString());
                    if (res) {
                        Helper.ToastNotify(RegisterActivity.this, "User Created Successfully.");
                        finish();
                    }
                }


            }
        });

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    ValidationResult validateCreate() {
        ValidationResult vr = new ValidationResult(false, "");

        if (edUserName.getText().toString().length() == 0) {
            vr.setHasError(true);
            vr.setErrorMessage("Username can't be empty");
            return vr;
        }

        if (edUserName.getText().length() > 10) {
            vr.setHasError(true);
            vr.setErrorMessage("Username can't length can't be more than 10 characters");
            return vr;
        }


        if (edPwd1.getText().toString().length() == 0) {
            vr.setHasError(true);
            vr.setErrorMessage("Password can't be empty");
            return vr;
        }
        if (edPwd2.getText().toString().length() == 0) {
            vr.setHasError(true);
            vr.setErrorMessage("You must re-enter the password");
            return vr;
        }
        if (!edPwd1.getText().toString().equals(edPwd2.getText().toString())) {
            vr.setHasError(true);
            vr.setErrorMessage("Password are not matching");
            return vr;
        }

        if (_inventoryDatabase.isUsernameUsed(edUserName.getText().toString())) {
            vr.setHasError(true);
            vr.setErrorMessage("Username is already used.");
            return vr;
        }
        return vr;
    }
}