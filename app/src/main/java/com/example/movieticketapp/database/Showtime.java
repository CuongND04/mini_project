package com.example.movieticketapp.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "showtimes",
    foreignKeys = {
        @ForeignKey(entity = Movie.class, parentColumns = "id", childColumns = "movieId"),
        @ForeignKey(entity = Theater.class, parentColumns = "id", childColumns = "theaterId")
    })
public class Showtime {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int movieId;
    public int theaterId;
    public String dateTime; // format: "yyyy-MM-dd HH:mm"
    public double price;

    public Showtime(int movieId, int theaterId, String dateTime, double price) {
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.dateTime = dateTime;
        this.price = price;
    }
}
