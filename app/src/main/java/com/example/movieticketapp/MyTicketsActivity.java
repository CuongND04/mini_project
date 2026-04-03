package com.example.movieticketapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.adapter.TicketAdapter;
import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.Movie;
import com.example.movieticketapp.database.Showtime;
import com.example.movieticketapp.database.Theater;
import com.example.movieticketapp.database.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class MyTicketsActivity extends AppCompatActivity {

    private AppDatabase db;
    private TextView tvEmpty;
    private RecyclerView rvTickets;

    // Cache for search
    private List<Ticket> allTickets;
    private Map<Integer, Showtime> showtimeMap;
    private Map<Integer, Movie> movieMap;
    private Map<Integer, Theater> theaterMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_tickets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("MovieTicketApp", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        rvTickets = findViewById(R.id.rvTickets);
        tvEmpty = findViewById(R.id.tvEmpty);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                applyFilter(s.toString().trim().toLowerCase());
            }
        });

        loadTickets(userId);
    }

    private void loadTickets(int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            allTickets = db.ticketDao().getByUserId(userId);
            List<Movie> movies = db.movieDao().getAll();
            List<Theater> theaters = db.theaterDao().getAll();

            movieMap = new HashMap<>();
            for (Movie m : movies) movieMap.put(m.id, m);

            theaterMap = new HashMap<>();
            for (Theater t : theaters) theaterMap.put(t.id, t);

            showtimeMap = new HashMap<>();
            for (Ticket ticket : allTickets) {
                if (!showtimeMap.containsKey(ticket.showtimeId)) {
                    Showtime st = db.showtimeDao().getById(ticket.showtimeId);
                    if (st != null) showtimeMap.put(st.id, st);
                }
            }

            runOnUiThread(() -> applyFilter(""));
        });
    }

    private void applyFilter(String query) {
        if (allTickets == null) return;

        List<Ticket> filtered;
        if (query.isEmpty()) {
            filtered = allTickets;
        } else {
            filtered = new ArrayList<>();
            for (Ticket ticket : allTickets) {
                Showtime showtime = showtimeMap.get(ticket.showtimeId);
                if (showtime == null) continue;
                Movie movie = movieMap.get(showtime.movieId);
                Theater theater = theaterMap.get(showtime.theaterId);
                String movieName = movie != null ? movie.title.toLowerCase() : "";
                String theaterName = theater != null ? theater.name.toLowerCase() : "";

                if (movieName.contains(query) || theaterName.contains(query)) {
                    filtered.add(ticket);
                }
            }
        }

        if (filtered.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvTickets.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvTickets.setVisibility(View.VISIBLE);
            rvTickets.setAdapter(new TicketAdapter(filtered, showtimeMap, movieMap, theaterMap));
        }
    }
}
