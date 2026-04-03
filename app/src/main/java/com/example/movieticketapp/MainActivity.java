package com.example.movieticketapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.Promotion;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private TextView tvWelcome;
    private TextView btnLogin;
    private LinearLayout promoContainer;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("MovieTicketApp", MODE_PRIVATE);
        db = AppDatabase.getInstance(this);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogin = findViewById(R.id.btnLogin);
        promoContainer = findViewById(R.id.promoContainer);

        findViewById(R.id.btnMovies).setOnClickListener(v ->
                startActivity(new Intent(this, MoviesActivity.class)));

        findViewById(R.id.btnTheaters).setOnClickListener(v ->
                startActivity(new Intent(this, TheatersActivity.class)));

        findViewById(R.id.btnShowtimes).setOnClickListener(v ->
                startActivity(new Intent(this, ShowtimesActivity.class)));

        findViewById(R.id.btnMyTickets).setOnClickListener(v -> {
            if (isLoggedIn()) {
                startActivity(new Intent(this, MyTicketsActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        btnLogin.setOnClickListener(v -> {
            if (isLoggedIn()) {
                prefs.edit().clear().apply();
                updateUI();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        loadPromotions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private boolean isLoggedIn() {
        return prefs.getInt("userId", -1) != -1;
    }

    private void updateUI() {
        if (isLoggedIn()) {
            String fullName = prefs.getString("fullName", "");
            tvWelcome.setText("Xin chào, " + fullName + "!");
            btnLogin.setText("Đăng xuất");
        } else {
            tvWelcome.setText("Chào mừng bạn!");
            btnLogin.setText("Đăng nhập");
        }
    }

    private void loadPromotions() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Promotion> promos = db.promotionDao().getAllActive();
            runOnUiThread(() -> {
                promoContainer.removeAllViews();
                for (Promotion promo : promos) {
                    LinearLayout card = new LinearLayout(this);
                    card.setOrientation(LinearLayout.HORIZONTAL);
                    card.setBackgroundResource(R.drawable.bg_card_dark);
                    card.setPadding(32, 24, 32, 24);
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardParams.bottomMargin = 16;
                    card.setLayoutParams(cardParams);
                    card.setGravity(android.view.Gravity.CENTER_VERTICAL);

                    // Discount badge
                    TextView badge = new TextView(this);
                    badge.setText("-" + promo.discountPercent + "%");
                    badge.setTextSize(16);
                    badge.setTextColor(getColor(R.color.text_on_accent));
                    badge.setTypeface(null, android.graphics.Typeface.BOLD);
                    badge.setGravity(android.view.Gravity.CENTER);
                    badge.setBackgroundResource(R.drawable.bg_accent_rounded);
                    badge.setPadding(20, 12, 20, 12);

                    // Info
                    LinearLayout info = new LinearLayout(this);
                    info.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    infoParams.leftMargin = 24;
                    info.setLayoutParams(infoParams);

                    TextView tvCode = new TextView(this);
                    tvCode.setText("Mã: " + promo.code);
                    tvCode.setTextSize(15);
                    tvCode.setTextColor(getColor(R.color.accent));
                    tvCode.setTypeface(null, android.graphics.Typeface.BOLD);

                    TextView tvDesc = new TextView(this);
                    tvDesc.setText(promo.description);
                    tvDesc.setTextSize(12);
                    tvDesc.setTextColor(getColor(R.color.text_secondary));

                    info.addView(tvCode);
                    info.addView(tvDesc);

                    card.addView(badge);
                    card.addView(info);
                    promoContainer.addView(card);
                }
            });
        });
    }
}
