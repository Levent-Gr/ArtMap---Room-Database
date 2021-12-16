package com.leventgorgu.artmapjava.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.leventgorgu.artmapjava.Adapter.ArtPlaceAdapter;
import com.leventgorgu.artmapjava.Model.ArtPlace;
import com.leventgorgu.artmapjava.DataBase.ArtPlaceDao;
import com.leventgorgu.artmapjava.DataBase.ArtPlaceDataBase;
import com.leventgorgu.artmapjava.R;
import com.leventgorgu.artmapjava.databinding.ActivityMainBinding;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ArtPlaceDataBase dataBase = Room.databaseBuilder(getApplicationContext(),ArtPlaceDataBase.class,"ArtPlace").build();
        ArtPlaceDao artPlaceDao = dataBase.artPlaceDao();

        compositeDisposable.add(artPlaceDao.Query()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainActivity.this::handlerResponse));
    }
    public void handlerResponse(List<ArtPlace> artPlaceList){
        binding.RecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        ArtPlaceAdapter adapter = new ArtPlaceAdapter(artPlaceList);
        binding.RecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.artmap_add){
            Intent intent = new Intent(MainActivity.this, ArtActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}