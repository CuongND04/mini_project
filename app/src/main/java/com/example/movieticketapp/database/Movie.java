package com.example.movieticketapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String genre;
    public int duration; // minutes
    public String description;
    public String imageUrl; // URL hình ảnh poster phim

    public Movie(String title, String genre, int duration, String description, String imageUrl) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
