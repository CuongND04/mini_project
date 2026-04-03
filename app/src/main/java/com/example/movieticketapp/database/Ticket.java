package com.example.movieticketapp.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tickets",
    foreignKeys = {
        @ForeignKey(entity = Showtime.class, parentColumns = "id", childColumns = "showtimeId"),
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId")
    })
public class Ticket {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int showtimeId;
    public int userId;
    public String seatNumber;
    public String bookingTime;

    public Ticket(int showtimeId, int userId, String seatNumber, String bookingTime) {
        this.showtimeId = showtimeId;
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.bookingTime = bookingTime;
    }
}
