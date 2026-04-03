package com.example.movieticketapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PaymentDao {
    @Insert
    long insert(Payment payment);

    @Query("SELECT * FROM payments WHERE userId = :userId ORDER BY id DESC")
    List<Payment> getByUserId(int userId);
}
