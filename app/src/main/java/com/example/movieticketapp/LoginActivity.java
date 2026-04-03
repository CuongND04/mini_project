package com.example.movieticketapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movieticketapp.database.AppDatabase;
import com.example.movieticketapp.database.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword, etFullName;
    private TextInputLayout tilFullName;
    private MaterialButton btnLogin, btnToggleRegister;
    private TextView tvLoginTitle, tvLoginSubtitle;
    private boolean isRegisterMode = false;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        tilFullName = findViewById(R.id.tilFullName);
        btnLogin = findViewById(R.id.btnLogin);
        btnToggleRegister = findViewById(R.id.btnToggleRegister);
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        tvLoginSubtitle = findViewById(R.id.tvLoginSubtitle);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isRegisterMode) {
                String fullName = etFullName.getText().toString().trim();
                if (!validateRegister(username, password, fullName)) return;
                register(username, password, fullName);
            } else {
                login(username, password);
            }
        });

        btnToggleRegister.setOnClickListener(v -> {
            isRegisterMode = !isRegisterMode;
            if (isRegisterMode) {
                tilFullName.setVisibility(View.VISIBLE);
                tvLoginTitle.setText("Đăng ký");
                tvLoginSubtitle.setText("Tạo tài khoản mới để đặt vé");
                btnLogin.setText("Đăng ký");
                btnToggleRegister.setText("Đã có tài khoản? Đăng nhập");
            } else {
                tilFullName.setVisibility(View.GONE);
                tvLoginTitle.setText("Đăng nhập");
                tvLoginSubtitle.setText("Đăng nhập để đặt vé xem phim");
                btnLogin.setText("Đăng nhập");
                btnToggleRegister.setText("Chưa có tài khoản? Đăng ký");
            }
        });
    }

    private boolean validateRegister(String username, String password, String fullName) {
        if (username.length() < 4) {
            Toast.makeText(this, "Tên đăng nhập phải có ít nhất 4 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.contains(" ")) {
            Toast.makeText(this, "Tên đăng nhập không được chứa khoảng trắng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return false;
        }
        String[] words = fullName.trim().split("\\s+");
        if (words.length < 2) {
            Toast.makeText(this, "Họ tên phải có ít nhất 2 từ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void login(String username, String password) {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = db.userDao().login(username, password);
            runOnUiThread(() -> {
                if (user != null) {
                    saveLogin(user);
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void register(String username, String password, String fullName) {
        Executors.newSingleThreadExecutor().execute(() -> {
            User existing = db.userDao().getByUsername(username);
            if (existing != null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show());
                return;
            }
            User newUser = new User(username, password, fullName);
            long id = db.userDao().insert(newUser);
            newUser.id = (int) id;
            runOnUiThread(() -> {
                saveLogin(newUser);
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void saveLogin(User user) {
        SharedPreferences prefs = getSharedPreferences("MovieTicketApp", MODE_PRIVATE);
        prefs.edit()
                .putInt("userId", user.id)
                .putString("username", user.username)
                .putString("fullName", user.fullName)
                .apply();
    }
}
