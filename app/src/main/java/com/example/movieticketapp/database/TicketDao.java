package com.example.movieticketapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TicketDao {
    @Insert
    long insert(Ticket ticket);

    @Query("SELECT * FROM tickets WHERE userId = :userId ORDER BY id DESC")
    List<Ticket> getByUserId(int userId);

    @Query("SELECT seatNumber FROM tickets WHERE showtimeId = :showtimeId")
    List<String> getBookedSeats(int showtimeId);

    @Query("DELETE FROM tickets WHERE id = :ticketId")
    void deleteById(int ticketId);
}
