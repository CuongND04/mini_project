package com.example.movieticketapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PromotionDao {
    @Insert
    void insertAll(Promotion... promotions);

    @Query("SELECT * FROM promotions WHERE code = :code AND isActive = 1 LIMIT 1")
    Promotion getByCode(String code);

    @Query("SELECT * FROM promotions WHERE isActive = 1")
    List<Promotion> getAllActive();
}
