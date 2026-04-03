package com.example.movieticketapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.Movie;

import java.util.concurrent.Executors;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int movieId = getIntent().getIntExtra("movieId", -1);
        if (movieId == -1) {
            Toast.makeText(this, "Không tìm thấy phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(this);

        Executors.newSingleThreadExecutor().execute(() -> {
            Movie movie = db.movieDao().getById(movieId);
            if (movie == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không tìm thấy phim", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            runOnUiThread(() -> {
                TextView tvTitle = findViewById(R.id.tvTitle);
                TextView tvGenre = findViewById(R.id.tvGenre);
                TextView tvDuration = findViewById(R.id.tvDuration);
                TextView tvDescription = findViewById(R.id.tvDescription);
                ImageView ivPoster = findViewById(R.id.ivPoster);

                tvTitle.setText(movie.title);
                tvGenre.setText(movie.genre);
                tvDuration.setText("⏱ " + movie.duration + " phút");
                tvDescription.setText(movie.description);

                if (movie.imageUrl != null && !movie.imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(movie.imageUrl)
                            .placeholder(R.drawable.bg_poster_placeholder)
                            .error(R.drawable.bg_poster_placeholder)
                            .into(ivPoster);
                }

                findViewById(R.id.btnViewShowtimes).setOnClickListener(v -> {
                    Intent intent = new Intent(this, ShowtimesActivity.class);
                    intent.putExtra("movieId", movie.id);
                    intent.putExtra("movieTitle", movie.title);
                    startActivity(intent);
                });
            });
        });
    }
}
