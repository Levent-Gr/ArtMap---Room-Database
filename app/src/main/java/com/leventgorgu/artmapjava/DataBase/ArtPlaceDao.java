package com.leventgorgu.artmapjava.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.leventgorgu.artmapjava.Model.ArtPlace;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ArtPlaceDao  {

    @Insert
    Completable Insert(ArtPlace artPlace);

    @Query("SELECT * FROM ArtPlace")
    Flowable<List<ArtPlace>> Query();
}
