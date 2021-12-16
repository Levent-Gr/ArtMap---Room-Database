package com.leventgorgu.artmapjava.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ArtPlace implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name ="artName")
    public String artName ;

    @ColumnInfo(name ="artistName")
    public String artistName;

    @ColumnInfo(name = "year")
    public String year;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name ="longitude")
    public double longitude;

    @ColumnInfo(name="placeName")
    public String placeName;

    @ColumnInfo(name="selectedImage")
    public byte[] selectedImage;

    public ArtPlace(String artName,String artistName,String year,double latitude,double longitude,String placeName,byte[] selectedImage){
        this.artName=artName;
        this.artistName=artistName;
        this.year=year;
        this.latitude=latitude;
        this.longitude=longitude;
        this.placeName=placeName;
        this.selectedImage=selectedImage;
    }

}
