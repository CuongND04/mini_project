package com.example.movieticketapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.adapter.ShowtimeAdapter;
import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.Movie;
import com.example.movieticketapp.database.Showtime;
import com.example.movieticketapp.database.Theater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class ShowtimesActivity extends AppCompatActivity implements ShowtimeAdapter.OnShowtimeClickListener {

    private AppDatabase db;
    private SharedPreferences prefs;
    private RecyclerView rvShowtimes;
    private int filterMovieId = -1;
    private String searchQuery = "";

    // Cache
    private List<Showtime> allShowtimes;
    private Map<Integer, Movie> movieMap;
    private Map<Integer, Theater> theaterMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_showtimes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        prefs = getSharedPreferences("MovieTicketApp", MODE_PRIVATE);

        TextView tvTitle = findViewById(R.id.tvTitle);
        filterMovieId = getIntent().getIntExtra("movieId", -1);
        String movieTitle = getIntent().getStringExtra("movieTitle");

        if (filterMovieId != -1 && movieTitle != null) {
            tvTitle.setText("Lịch chiếu: " + movieTitle);
        }

        rvShowtimes = findViewById(R.id.rvShowtimes);
        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                searchQuery = s.toString().trim().toLowerCase();
                applyFilter();
            }
        });

        loadShowtimes();
    }

    private void loadShowtimes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (filterMovieId != -1) {
                allShowtimes = db.showtimeDao().getByMovieId(filterMovieId);
            } else {
                allShowtimes = db.showtimeDao().getAll();
            }

            List<Movie> movies = db.movieDao().getAll();
            List<Theater> theaters = db.theaterDao().getAll();

            movieMap = new HashMap<>();
            for (Movie m : movies) movieMap.put(m.id, m);

            theaterMap = new HashMap<>();
            for (Theater t : theaters) theaterMap.put(t.id, t);

            runOnUiThread(this::applyFilter);
        });
    }

    private void applyFilter() {
        if (allShowtimes == null) return;

        List<Showtime> filtered;
        if (searchQuery.isEmpty()) {
            filtered = allShowtimes;
        } else {
            filtered = new ArrayList<>();
            for (Showtime st : allShowtimes) {
                Movie movie = movieMap.get(st.movieId);
                Theater theater = theaterMap.get(st.theaterId);
                String movieName = movie != null ? movie.title.toLowerCase() : "";
                String theaterName = theater != null ? theater.name.toLowerCase() : "";
                String date = st.dateTime.toLowerCase();

                if (movieName.contains(searchQuery) || theaterName.contains(searchQuery) || date.contains(searchQuery)) {
                    filtered.add(st);
                }
            }
        }

        ShowtimeAdapter adapter = new ShowtimeAdapter(filtered, movieMap, theaterMap, this);
        rvShowtimes.setAdapter(adapter);
    }

    @Override
    public void onBookClick(Showtime showtime) {
        int userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập trước khi đặt vé", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        Intent intent = new Intent(this, BookTicketActivity.class);
        intent.putExtra("showtimeId", showtime.id);
        startActivity(intent);
    }
}
