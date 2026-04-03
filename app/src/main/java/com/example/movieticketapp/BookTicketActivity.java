package com.example.movieticketapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.adapter.SeatAdapter;
import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.Movie;
import com.example.movieticketapp.database.Showtime;
import com.example.movieticketapp.database.Theater;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;

public class BookTicketActivity extends AppCompatActivity {

    private AppDatabase db;
    private int showtimeId;
    private int userId;
    private double pricePerSeat;
    private String movieTitle, theaterName, dateTime;
    private TextView tvSelectedSeat, tvPrice, btnConfirm;
    private Set<String> selectedSeats = new TreeSet<>();

    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_ticket);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("MovieTicketApp", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        showtimeId = getIntent().getIntExtra("showtimeId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvSelectedSeat = findViewById(R.id.tvSelectedSeat);
        tvPrice = findViewById(R.id.tvPrice);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        btnConfirm.setOnClickListener(v -> goToPayment());

        loadShowtimeInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload dữ liệu ghế từ DB mỗi khi quay lại màn hình
        selectedSeats.clear();
        loadShowtimeInfo();
    }

    private void loadShowtimeInfo() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Showtime showtime = db.showtimeDao().getById(showtimeId);
            if (showtime == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Không tìm thấy lịch chiếu", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            Movie movie = db.movieDao().getById(showtime.movieId);
            Theater theater = db.theaterDao().getById(showtime.theaterId);
            List<String> bookedSeats = db.ticketDao().getBookedSeats(showtimeId);

            pricePerSeat = showtime.price;
            movieTitle = movie != null ? movie.title : "";
            theaterName = theater != null ? theater.name : "";
            dateTime = showtime.dateTime;

            List<String> allSeats = new ArrayList<>();
            int totalSeats = theater != null ? theater.totalSeats : 60;
            int seatsPerRow = 6;
            int rows = Math.min(totalSeats / seatsPerRow, 10);
            for (int r = 0; r < rows; r++) {
                for (int s = 1; s <= seatsPerRow; s++) {
                    allSeats.add((char) ('A' + r) + "" + s);
                }
            }

            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.tvMovieTitle)).setText(movieTitle);
                ((TextView) findViewById(R.id.tvTheaterName)).setText(theaterName);
                ((TextView) findViewById(R.id.tvDateTime)).setText(dateTime);
                tvPrice.setText(nf.format(pricePerSeat) + " VNĐ");
                tvSelectedSeat.setText("Chưa chọn ghế");
                btnConfirm.setAlpha(0.5f);
                btnConfirm.setEnabled(false);

                RecyclerView rvSeats = findViewById(R.id.rvSeats);
                rvSeats.setLayoutManager(new GridLayoutManager(this, seatsPerRow));
                SeatAdapter seatAdapter = new SeatAdapter(allSeats, bookedSeats, seats -> {
                    selectedSeats = seats;
                    if (seats.isEmpty()) {
                        tvSelectedSeat.setText("Chưa chọn ghế");
                        tvPrice.setText(nf.format(pricePerSeat) + " VNĐ");
                        btnConfirm.setAlpha(0.5f);
                        btnConfirm.setEnabled(false);
                    } else {
                        String seatStr = String.join(", ", new TreeSet<>(seats));
                        tvSelectedSeat.setText("Ghế: " + seatStr + " (" + seats.size() + " vé)");
                        double total = pricePerSeat * seats.size();
                        tvPrice.setText(nf.format(total) + " VNĐ");
                        btnConfirm.setAlpha(1.0f);
                        btnConfirm.setEnabled(true);
                    }
                });
                rvSeats.setAdapter(seatAdapter);
            });
        });
    }

    private void goToPayment() {
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ghế", Toast.LENGTH_SHORT).show();
            return;
        }

        String seatStr = String.join(", ", new TreeSet<>(selectedSeats));

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("showtimeId", showtimeId);
        intent.putExtra("movieTitle", movieTitle);
        intent.putExtra("theaterName", theaterName);
        intent.putExtra("dateTime", dateTime);
        intent.putExtra("seatNumbers", seatStr);
        intent.putExtra("pricePerSeat", pricePerSeat);
        intent.putExtra("seatCount", selectedSeats.size());
        startActivity(intent);
    }
}
