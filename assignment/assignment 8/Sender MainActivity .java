package com.example.sender;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    ImageView imageView;
    static final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        Button sendTextButton = findViewById(R.id.send_text_button);
        Button sendImageButton = findViewById(R.id.send_image_button);
        Button chooseImageButton = findViewById(R.id.choose_image_button);
        imageView = findViewById(R.id.image_view);

        sendTextButton.setOnClickListener(v -> sendText());
        sendImageButton.setOnClickListener(v -> sendImage());
        chooseImageButton.setOnClickListener(v -> openGallery());
    }

    private void sendText() {
        String textToSend = editText.getText().toString();

        if (!textToSend.isEmpty()) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
    }

    private void sendImage() {
        if (imageUri != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            sendIntent.setType("image/*");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE); // Make ImageView visible
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
