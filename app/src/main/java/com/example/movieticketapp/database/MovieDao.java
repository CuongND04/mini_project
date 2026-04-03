package com.example.movieticketapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert
    void insertAll(Movie... movies);

    @Query("SELECT * FROM movies")
    List<Movie> getAll();

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    Movie getById(int id);

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%'")
    List<Movie> search(String query);

    @Query("SELECT DISTINCT genre FROM movies")
    List<String> getAllGenres();

    @Query("SELECT * FROM movies WHERE genre = :genre")
    List<Movie> getByGenre(String genre);

    @Query("SELECT * FROM movies WHERE genre = :genre AND title LIKE '%' || :query || '%'")
    List<Movie> searchByGenre(String genre, String query);
}
