package com.leventgorgu.artmapjava.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.leventgorgu.artmapjava.DataBase.ArtPlaceDao;
import com.leventgorgu.artmapjava.Model.ArtPlace;

@Database(entities = {ArtPlace.class},version = 1)
public abstract class ArtPlaceDataBase extends RoomDatabase {

    public abstract ArtPlaceDao artPlaceDao();
}
