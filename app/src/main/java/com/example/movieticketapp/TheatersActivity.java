package com.example.movieticketapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.adapter.TheaterAdapter;
import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.Theater;

import java.util.List;
import java.util.concurrent.Executors;

public class TheatersActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecyclerView rvTheaters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_theaters);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        rvTheaters = findViewById(R.id.rvTheaters);
        rvTheaters.setLayoutManager(new LinearLayoutManager(this));

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                filterTheaters(s.toString().trim());
            }
        });

        filterTheaters("");
    }

    private void filterTheaters(String query) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Theater> theaters;
            if (query.isEmpty()) {
                theaters = db.theaterDao().getAll();
            } else {
                theaters = db.theaterDao().search(query);
            }
            runOnUiThread(() -> rvTheaters.setAdapter(new TheaterAdapter(theaters)));
        });
    }
}
