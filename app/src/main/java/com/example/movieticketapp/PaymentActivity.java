package com.example.movieticketapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.Payment;
import com.example.movieticketapp.database.Promotion;
import com.example.movieticketapp.database.Ticket;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class PaymentActivity extends AppCompatActivity {

    private AppDatabase db;
    private int userId, showtimeId;
    private String movieTitle, theaterName, dateTime, seatNumbers;
    private double pricePerSeat;
    private int seatCount;
    private double totalAmount, discountAmount, finalAmount;
    private int discountPercent = 0;
    private String appliedPromoCode = "";
    private String selectedMethod = "";

    private TextView tvSubtotal, tvTotalAmount, tvDiscountLabel, tvPromoResult, tvLoadingText, tvPromoHint;
    private EditText etPromoCode;
    private FrameLayout loadingOverlay;
    private LinearLayout btnMomo, btnZaloPay, btnCard;
    private View radioMomo, radioZalo, radioCard;

    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("MovieTicketApp", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        showtimeId = getIntent().getIntExtra("showtimeId", -1);
        movieTitle = getIntent().getStringExtra("movieTitle");
        theaterName = getIntent().getStringExtra("theaterName");
        dateTime = getIntent().getStringExtra("dateTime");
        seatNumbers = getIntent().getStringExtra("seatNumbers");
        pricePerSeat = getIntent().getDoubleExtra("pricePerSeat", 0);
        seatCount = getIntent().getIntExtra("seatCount", 1);

        totalAmount = pricePerSeat * seatCount;
        finalAmount = totalAmount;

        initViews();
        displayOrderInfo();
        setupPaymentMethods();
        setupPromoCode();
        loadPromoHints();

        findViewById(R.id.btnPay).setOnClickListener(v -> processPayment());
    }

    private void initViews() {
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvDiscountLabel = findViewById(R.id.tvDiscountLabel);
        tvPromoResult = findViewById(R.id.tvPromoResult);
        tvLoadingText = findViewById(R.id.tvLoadingText);
        tvPromoHint = findViewById(R.id.tvPromoHint);
        etPromoCode = findViewById(R.id.etPromoCode);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        btnMomo = findViewById(R.id.btnMomo);
        btnZaloPay = findViewById(R.id.btnZaloPay);
        btnCard = findViewById(R.id.btnCard);
        radioMomo = findViewById(R.id.radioMomo);
        radioZalo = findViewById(R.id.radioZalo);
        radioCard = findViewById(R.id.radioCard);
    }

    private void displayOrderInfo() {
        ((TextView) findViewById(R.id.tvMovieTitle)).setText(movieTitle);
        ((TextView) findViewById(R.id.tvTheaterName)).setText("🏢 " + theaterName);
        ((TextView) findViewById(R.id.tvDateTime)).setText("🕐 " + dateTime);
        ((TextView) findViewById(R.id.tvSeats)).setText("💺 Ghế: " + seatNumbers + " (" + seatCount + " vé)");
        tvSubtotal.setText(nf.format(totalAmount) + " VNĐ");
        tvTotalAmount.setText(nf.format(finalAmount) + " VNĐ");
    }

    private void loadPromoHints() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Promotion> promos = db.promotionDao().getAllActive();
            StringBuilder sb = new StringBuilder("Mã hiện có: ");
            for (int i = 0; i < promos.size(); i++) {
                sb.append(promos.get(i).code);
                if (i < promos.size() - 1) sb.append(", ");
            }
            runOnUiThread(() -> {
                if (tvPromoHint != null) {
                    tvPromoHint.setText(sb.toString());
                    tvPromoHint.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void setupPaymentMethods() {
        View.OnClickListener methodClick = v -> {
            btnMomo.setBackgroundResource(R.drawable.bg_payment_method);
            btnZaloPay.setBackgroundResource(R.drawable.bg_payment_method);
            btnCard.setBackgroundResource(R.drawable.bg_payment_method);
            radioMomo.setBackgroundResource(R.drawable.bg_accent_outline);
            radioZalo.setBackgroundResource(R.drawable.bg_accent_outline);
            radioCard.setBackgroundResource(R.drawable.bg_accent_outline);

            int id = v.getId();
            if (id == R.id.btnMomo) {
                selectedMethod = "MoMo";
                btnMomo.setBackgroundResource(R.drawable.bg_payment_method_selected);
                radioMomo.setBackgroundResource(R.drawable.bg_accent_rounded);
            } else if (id == R.id.btnZaloPay) {
                selectedMethod = "ZaloPay";
                btnZaloPay.setBackgroundResource(R.drawable.bg_payment_method_selected);
                radioZalo.setBackgroundResource(R.drawable.bg_accent_rounded);
            } else if (id == R.id.btnCard) {
                selectedMethod = "Thẻ ngân hàng";
                btnCard.setBackgroundResource(R.drawable.bg_payment_method_selected);
                radioCard.setBackgroundResource(R.drawable.bg_accent_rounded);
            }
        };

        btnMomo.setOnClickListener(methodClick);
        btnZaloPay.setOnClickListener(methodClick);
        btnCard.setOnClickListener(methodClick);
    }

    private void setupPromoCode() {
        findViewById(R.id.btnApplyPromo).setOnClickListener(v -> {
            String code = etPromoCode.getText().toString().trim().toUpperCase();
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã khuyến mãi", Toast.LENGTH_SHORT).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                Promotion promo = db.promotionDao().getByCode(code);
                runOnUiThread(() -> {
                    if (promo != null) {
                        discountPercent = promo.discountPercent;
                        appliedPromoCode = code;
                        discountAmount = totalAmount * discountPercent / 100;
                        finalAmount = totalAmount - discountAmount;

                        tvPromoResult.setText("✅ " + promo.description + " (-" + discountPercent + "%)");
                        tvPromoResult.setTextColor(getColor(R.color.success));
                        tvPromoResult.setVisibility(View.VISIBLE);

                        tvDiscountLabel.setText("Giảm " + discountPercent + "%: -" + nf.format(discountAmount) + " VNĐ");
                        tvDiscountLabel.setVisibility(View.VISIBLE);
                        tvTotalAmount.setText(nf.format(finalAmount) + " VNĐ");
                    } else {
                        discountPercent = 0;
                        discountAmount = 0;
                        finalAmount = totalAmount;
                        appliedPromoCode = "";

                        tvPromoResult.setText("❌ Mã khuyến mãi không hợp lệ");
                        tvPromoResult.setTextColor(getColor(R.color.error));
                        tvPromoResult.setVisibility(View.VISIBLE);

                        tvDiscountLabel.setVisibility(View.GONE);
                        tvTotalAmount.setText(nf.format(finalAmount) + " VNĐ");
                    }
                });
            });
        });
    }

    private void processPayment() {
        if (selectedMethod.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingOverlay.setVisibility(View.VISIBLE);
        tvLoadingText.setText("Đang xử lý thanh toán...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tvLoadingText.setText("Đang xác nhận giao dịch...");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                String paymentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(new Date());

                Executors.newSingleThreadExecutor().execute(() -> {
                    String[] seats = seatNumbers.split(", ");
                    for (String seat : seats) {
                        Ticket ticket = new Ticket(showtimeId, userId, seat.trim(), paymentTime);
                        db.ticketDao().insert(ticket);
                    }

                    Payment payment = new Payment(userId, showtimeId, seatNumbers,
                            totalAmount, discountAmount, finalAmount,
                            selectedMethod, appliedPromoCode, paymentTime);
                    db.paymentDao().insert(payment);

                    runOnUiThread(() -> {
                        loadingOverlay.setVisibility(View.GONE);
                        Toast.makeText(this, "🎉 Thanh toán thành công!", Toast.LENGTH_LONG).show();

                        // Quay về Home và xóa sạch lịch sử màn hình
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                });
            }, 1500);
        }, 1500);
    }
}
