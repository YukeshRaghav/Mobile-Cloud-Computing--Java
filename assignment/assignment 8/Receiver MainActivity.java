package com.example.receiver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView receivedTextView;
    ImageView receivedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receivedTextView = findViewById(R.id.received_text_view);
        receivedImageView = findViewById(R.id.received_image_view);

        Intent receivedIntent = getIntent();
        String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        if (receivedText != null) {
            receivedTextView.setText(receivedText);
        }

        Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (receivedUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), receivedUri);
                receivedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
