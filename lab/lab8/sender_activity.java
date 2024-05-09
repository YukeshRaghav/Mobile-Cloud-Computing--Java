package com.example.lab8;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class sender_activity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sender_activity);
        editText=findViewById(R.id.EditTextID);
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void SendMessage(View view) {
        String msg = editText.getText().toString();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }

//        Intent shareIntent = Intent.createChooser(sendIntent, null);
//        startActivity(shareIntent);

    }
}