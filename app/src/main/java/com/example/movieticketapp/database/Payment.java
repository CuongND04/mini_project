package com.example.movieticketapp.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "payments",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
        @ForeignKey(entity = Showtime.class, parentColumns = "id", childColumns = "showtimeId")
    })
public class Payment {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int showtimeId;
    public String seatNumbers; // ghế cách nhau bởi dấu phẩy: "A1,A2,A3"
    public double totalAmount;
    public double discountAmount;
    public double finalAmount;
    public String paymentMethod;
    public String promotionCode;
    public String paymentTime;

    public Payment(int userId, int showtimeId, String seatNumbers,
                   double totalAmount, double discountAmount, double finalAmount,
                   String paymentMethod, String promotionCode, String paymentTime) {
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatNumbers = seatNumbers;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.promotionCode = promotionCode;
        this.paymentTime = paymentTime;
    }
}
