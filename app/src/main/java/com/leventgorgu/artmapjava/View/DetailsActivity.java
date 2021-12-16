package com.leventgorgu.artmapjava.View;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leventgorgu.artmapjava.Model.ArtPlace;
import com.leventgorgu.artmapjava.R;
//import com.leventgorgu.artmapjava.View.databinding.ActivityDetailsBinding;
import com.leventgorgu.artmapjava.databinding.ActivityDetailsBinding;

public class DetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityDetailsBinding binding;
    ArtPlace artPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        artPlace = (ArtPlace) intent.getSerializableExtra("artPlace");

        binding.artNameText.setText(artPlace.artName);
        binding.artistNameText.setText(artPlace.artistName);
        binding.yearText.setText(artPlace.year);

        binding.placeNameText.setText(artPlace.placeName);

        Bitmap bitmap = BitmapFactory.decodeByteArray(artPlace.selectedImage,0,artPlace.selectedImage.length);
        binding.imageView2.setImageBitmap(bitmap);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng latLng = new LatLng(artPlace.latitude, artPlace.longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(artPlace.placeName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
    }
}