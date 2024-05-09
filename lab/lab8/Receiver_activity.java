package com.example.lab8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Receiver_activity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiver_activity);
        textView= findViewById(R.id.textViewID);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(intent.ACTION_SEND.equals(action))
        {
            if("text/plain".equals(type))
            {
                String msg = intent.getStringExtra(intent.EXTRA_TEXT);
                textView.setText("message from sender: " + msg);
            }
        }
    }
}