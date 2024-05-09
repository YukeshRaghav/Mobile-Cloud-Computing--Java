package com.example.lab10;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView text1;
    private ActivityResultLauncher<Intent> googlePickerActivityResultPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = findViewById(R.id.text1);

        AccountManager accountManager = AccountManager.get(getApplicationContext());
        Intent googlePicker = AccountManager.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, null, null, null, null);

        googlePickerActivityResultPicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getExtras() != null) {
                                String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                                Account selectedAccount = new Account(accountName, "com.google");
                                getAuthToken(selectedAccount);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Account selection canceled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        googlePickerActivityResultPicker.launch(googlePicker);
    }

    private void getAuthToken(Account account) {
        AccountManager accountManager = AccountManager.get(MainActivity.this);
        accountManager.getAuthToken(
                account,
                "oauth2:https://www.googleapis.com/auth/userinfo.profile",
                null,
                MainActivity.this,
                accountManagerFuture -> {
                    try {
                        String token = accountManagerFuture.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                        updateTokenTextView(token);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error getting token", Toast.LENGTH_SHORT).show();
                    }
                },
                null
        );
    }

    private void updateTokenTextView(String token) {
        runOnUiThread(() -> text1.setText(String.format("Token Value: %s", token)));
    }
}
