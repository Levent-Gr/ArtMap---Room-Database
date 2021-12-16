package com.leventgorgu.artmapjava.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.leventgorgu.artmapjava.databinding.ActivityArtBinding;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Bitmap selectedImage;
    private ActivityArtBinding binding;
    byte[] bytesArray;
    ByteArrayOutputStream byteArrayOutputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();
    }

    public void goMap(View view){

        if(binding.artNameText.getText()==null){
            Toast.makeText(ArtActivity.this, "Enter ArtName", Toast.LENGTH_SHORT).show();
        }else{
            Intent intentToMap = new Intent(ArtActivity.this, MapsActivity.class);

            Bitmap smallImage = makeSmallerImage(selectedImage,300);
            byteArrayOutputStream = new ByteArrayOutputStream();


            smallImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);

            bytesArray = new byte[byteArrayOutputStream.size()];
            bytesArray = byteArrayOutputStream.toByteArray();

            intentToMap.putExtra("image",bytesArray);
            intentToMap.putExtra("artName",binding.artNameText.getText().toString());
            intentToMap.putExtra("artistName",binding.artistNameText.getText().toString());
            intentToMap.putExtra("year",binding.yearText.getText().toString());

            startActivity(intentToMap);
        }
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    public void imageSelect(View view){
        if(ContextCompat.checkSelfPermission(ArtActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
           if(ActivityCompat.shouldShowRequestPermissionRationale(ArtActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){

               Snackbar.make(view,"Permission nedeed !",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                   }
               }).show();
           }else{
               permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
           }

        }else{
            Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    public void registerLauncher(){

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result==true){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK){//--Veri geldimi gelmedimi

                    Intent imageintent = result.getData();
                    if (imageintent!=null){
                        Uri image = imageintent.getData();


                    try {
                        if(Build.VERSION.SDK_INT>=28){
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),image);
                            selectedImage = ImageDecoder.decodeBitmap(source);
                            binding.imageView.setImageBitmap(selectedImage);
                        }else{
                            selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(),image);
                            binding.imageView.setImageBitmap(selectedImage);
                        }

                    }catch (Exception exception){
                        exception.printStackTrace();
                    }

                    }
                }
            }
        });
    }
}