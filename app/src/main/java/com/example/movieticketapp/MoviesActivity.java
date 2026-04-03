package com.example.movieticketapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.adapter.MovieAdapter;
import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.Movie;

import java.util.List;
import java.util.concurrent.Executors;

public class MoviesActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    private RecyclerView rvMovies;
    private AppDatabase db;
    private String currentGenre = "";  // "" = Tất cả
    private String currentSearch = "";
    private LinearLayout chipContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movies);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        chipContainer = findViewById(R.id.chipContainer);

        // Search
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                currentSearch = s.toString().trim();
                filterMovies();
            }
        });

        loadGenreChips();
        filterMovies();
    }

    private void loadGenreChips() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> genres = db.movieDao().getAllGenres();
            runOnUiThread(() -> {
                chipContainer.removeAllViews();

                // "Tất cả" chip
                addChip("Tất cả", "", true);

                for (String genre : genres) {
                    addChip(genre, genre, false);
                }
            });
        });
    }

    private void addChip(String label, String genre, boolean active) {
        TextView chip = new TextView(this);
        chip.setText(label);
        chip.setTextSize(13);
        chip.setPadding(32, 16, 32, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(8);
        chip.setLayoutParams(params);

        if (active) {
            chip.setBackgroundResource(R.drawable.bg_genre_chip_active);
            chip.setTextColor(getColor(R.color.text_on_accent));
        } else {
            chip.setBackgroundResource(R.drawable.bg_genre_chip);
            chip.setTextColor(getColor(R.color.text_primary));
        }

        chip.setOnClickListener(v -> {
            currentGenre = genre;
            // Reset all chips
            for (int i = 0; i < chipContainer.getChildCount(); i++) {
                TextView c = (TextView) chipContainer.getChildAt(i);
                c.setBackgroundResource(R.drawable.bg_genre_chip);
                c.setTextColor(getColor(R.color.text_primary));
            }
            chip.setBackgroundResource(R.drawable.bg_genre_chip_active);
            chip.setTextColor(getColor(R.color.text_on_accent));
            filterMovies();
        });

        chipContainer.addView(chip);
    }

    private void filterMovies() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Movie> movies;
            if (currentGenre.isEmpty() && currentSearch.isEmpty()) {
                movies = db.movieDao().getAll();
            } else if (currentGenre.isEmpty()) {
                movies = db.movieDao().search(currentSearch);
            } else if (currentSearch.isEmpty()) {
                movies = db.movieDao().getByGenre(currentGenre);
            } else {
                movies = db.movieDao().searchByGenre(currentGenre, currentSearch);
            }
            runOnUiThread(() -> {
                MovieAdapter adapter = new MovieAdapter(movies, this);
                rvMovies.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onViewShowtimes(Movie movie) {
        Intent intent = new Intent(this, ShowtimesActivity.class);
        intent.putExtra("movieId", movie.id);
        intent.putExtra("movieTitle", movie.title);
        startActivity(intent);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movieId", movie.id);
        startActivity(intent);
    }
}
