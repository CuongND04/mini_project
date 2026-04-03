package com.example.movieticketapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "promotions")
public class Promotion {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String code;
    public int discountPercent;
    public String description;
    public boolean isActive;

    public Promotion(String code, int discountPercent, String description, boolean isActive) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.description = description;
        this.isActive = isActive;
    }
}
