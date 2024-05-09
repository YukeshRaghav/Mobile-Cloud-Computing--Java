package com.example.lab6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView imageView = findViewById(R.id.imageID);
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }

    private void dispatchTakePictureIntent() {
        File photoFile = new File(getExternalCacheDir(),"output_image.jpg");
        try{
            if(photoFile.exists()){
                photoFile.delete();
            }
            photoFile.createNewFile();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        Uri photoUri = null;
        if (Build.VERSION.SDK_INT >= 24){
            photoUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.example.lab6.fileprovider",
                    photoFile);
        }else {
            photoUri = Uri.fromFile(photoFile);
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
