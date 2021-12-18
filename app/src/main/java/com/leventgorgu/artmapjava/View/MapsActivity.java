package com.leventgorgu.artmapjava.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.leventgorgu.artmapjava.Model.ArtPlace;
import com.leventgorgu.artmapjava.DataBase.ArtPlaceDao;
import com.leventgorgu.artmapjava.DataBase.ArtPlaceDataBase;
import com.leventgorgu.artmapjava.R;
import com.leventgorgu.artmapjava.databinding.ActivityMapsBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    ActivityResultLauncher<String> permissionLauncherForMap;
    LocationManager locationManager;
    LocationListener locationListener;

    boolean info;
    SharedPreferences sharedPreferences;

    double selectedLatitude;
    double selectedLongitude;

    Intent intentFromArtActivity;
    ArtPlaceDao artPlaceDao;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ArtPlaceDataBase dataBase = Room.databaseBuilder(getApplicationContext(),ArtPlaceDataBase.class,"ArtPlace").build();
        artPlaceDao = dataBase.artPlaceDao();

        info = false;
        sharedPreferences = getSharedPreferences("com.leventgorgu.artmapjava",MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("info",false).apply();

        intentFromArtActivity = getIntent();

        registerLauncher();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);



        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                info = sharedPreferences.getBoolean("info",false);
                if (!info) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    sharedPreferences.edit().putBoolean("info",true).apply();
                }
            }
        };


        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.getRoot(),"Permission Needed!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission !", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionLauncherForMap.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }else{
                permissionLauncherForMap.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation!=null){
                LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));

            }
            mMap.setMyLocationEnabled(true);
        }

    }


    public void save(View view){
        String placeName = binding.placeNameText.getText().toString();
        double latitude = selectedLatitude;
        double longitude = selectedLongitude;

        byte[] selectedImage = intentFromArtActivity.getByteArrayExtra("image");
        String artName = intentFromArtActivity.getStringExtra("artName");
        String artistName= intentFromArtActivity.getStringExtra("artistName");
        String year = intentFromArtActivity.getStringExtra("year");

        ArtPlace artPlace = new ArtPlace(artName,artistName,year,latitude,longitude,placeName,selectedImage);

        compositeDisposable.add(artPlaceDao.Insert(artPlace)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MapsActivity.this::handleResponse));
    }
    private void handleResponse(){
        Intent intentToMain = new Intent(MapsActivity.this, MainActivity.class);
        intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentToMain);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    public void registerLauncher(){
        permissionLauncherForMap = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result==true){
                    if(ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation!=null){
                            LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));

                        }
                        mMap.setMyLocationEnabled(true);

                    }
                }else{
                    Toast.makeText(MapsActivity.this, "Permission Needed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(binding.placeNameText.getText().toString()));

        selectedLatitude = latLng.latitude;
        selectedLongitude = latLng.longitude;


    }
}
