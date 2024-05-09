package com.example.lab10;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import java.io.IOException;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


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
                new String[] {"com.google"}, null, null, null, null);

        googlePickerActivityResultPicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Bundle options = new Bundle();

                            AccountManager accountManager = AccountManager.get(MainActivity.this);
                            Account[] accounts = accountManager.getAccounts();

                            for(Account a: accounts){
                                accountManager.invalidateAuthToken(a.type, null);
                                accountManager.getAuthToken(
                                        a,
                                        "oauth2:https://www.googleapis.com/auth/userinfo.profile",
                                        options,
                                        MainActivity.this,
                                        new OnTokenAcquired(),
                                        new android.os.Handler(new OnError()));
                            }
                        }
                    }
                }
        );

        googlePickerActivityResultPicker.launch(googlePicker);
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

        public class NetworkThread implements Runnable {
            private final AccountManagerFuture<Bundle> result;

            public NetworkThread(AccountManagerFuture<Bundle> result) {
                this.result = result;
            }

            @Override
            public void run() {
                try {
                    Intent launch = (Intent) result.getResult().get(AccountManager.KEY_INTENT);
                    if (launch != null) {
                        googlePickerActivityResultPicker.launch(launch);
                        return;
                    }

                    Bundle bundle = result.getResult();
                    String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                    text1.setText(String.format("Token Value: %s", token));
                    System.out.println(token);

                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
            NetworkThread networkThread = new NetworkThread(accountManagerFuture);
            networkThread.run();
        }
    }

    private class Handler extends android.os.Handler {
        public Handler(OnError onError) {
        }
    }

    private class OnError implements android.os.Handler.Callback {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            return true;
        }
    }
}