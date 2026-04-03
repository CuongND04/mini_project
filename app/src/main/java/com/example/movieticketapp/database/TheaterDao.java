package com.example.movieticketapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TheaterDao {
    @Insert
    void insertAll(Theater... theaters);

    @Query("SELECT * FROM theaters")
    List<Theater> getAll();

    @Query("SELECT * FROM theaters WHERE id = :id LIMIT 1")
    Theater getById(int id);

    @Query("SELECT * FROM theaters WHERE name LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%'")
    List<Theater> search(String query);
}
