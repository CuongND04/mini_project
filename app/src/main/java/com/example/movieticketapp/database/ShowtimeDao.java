package com.example.movieticketapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ShowtimeDao {
    @Insert
    void insertAll(Showtime... showtimes);

    @Query("SELECT * FROM showtimes")
    List<Showtime> getAll();

    @Query("SELECT * FROM showtimes WHERE movieId = :movieId")
    List<Showtime> getByMovieId(int movieId);

    @Query("SELECT * FROM showtimes WHERE theaterId = :theaterId")
    List<Showtime> getByTheaterId(int theaterId);

    @Query("SELECT * FROM showtimes WHERE id = :id LIMIT 1")
    Showtime getById(int id);
}
