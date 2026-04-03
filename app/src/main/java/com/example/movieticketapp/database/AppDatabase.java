package com.example.movieticketapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {User.class, Movie.class, Theater.class, Showtime.class,
        Ticket.class, Payment.class, Promotion.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract MovieDao movieDao();
    public abstract TheaterDao theaterDao();
    public abstract ShowtimeDao showtimeDao();
    public abstract TicketDao ticketDao();
    public abstract PaymentDao paymentDao();
    public abstract PromotionDao promotionDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "movie_ticket_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(new SeedDatabaseCallback())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static class SeedDatabaseCallback extends RoomDatabase.Callback {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase database = INSTANCE;
                if (database == null) return;

                // Seed Users
                database.userDao().insert(new User("admin", "123456", "Admin User"));
                database.userDao().insert(new User("user1", "123456", "Nguyễn Văn A"));
                database.userDao().insert(new User("user2", "123456", "Trần Thị B"));

                // Seed Movies
                database.movieDao().insertAll(
                    new Movie("Avengers: Endgame", "Hành động", 181,
                        "Biệt đội siêu anh hùng tập hợp một lần nữa để đảo ngược hành động của Thanos và khôi phục sự cân bằng cho vũ trụ.",
                        "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg"),

                    new Movie("Parasite", "Kinh dị", 132,
                        "Lòng tham và sự phân biệt giai cấp đe dọa mối quan hệ cộng sinh mới hình thành giữa gia đình Park giàu có và gia đình Kim nghèo khó.",
                        "https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg"),

                    new Movie("Spider-Man: No Way Home", "Hành động", 148,
                        "Khi danh tính của Spider-Man bị tiết lộ, Peter nhờ Doctor Strange giúp đỡ.",
                        "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg"),

                    new Movie("The Batman", "Hành động", 176,
                        "Khi một kẻ giết người hàng loạt bắt đầu sát hại các nhân vật chính trị quan trọng ở Gotham, Batman buộc phải điều tra.",
                        "https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T25onhq.jpg"),

                    new Movie("Everything Everywhere All at Once", "Khoa học viễn tưởng", 139,
                        "Một phụ nữ nhập cư Trung Quốc bị cuốn vào một cuộc phiêu lưu điên rồ, nơi chỉ mình cô có thể cứu thế giới.",
                        "https://image.tmdb.org/t/p/w500/w3LxiVYdWWRvEVdn5RYq6jIqkb1.jpg")
                );

                // Seed Theaters
                database.theaterDao().insertAll(
                    new Theater("CGV Vincom Center", "72 Lê Thánh Tôn, Q.1, TP.HCM", 120),
                    new Theater("Lotte Cinema Nam Sài Gòn", "469 Nguyễn Hữu Thọ, Q.7, TP.HCM", 100),
                    new Theater("Galaxy Nguyễn Du", "116 Nguyễn Du, Q.1, TP.HCM", 80)
                );

                // Seed Showtimes
                database.showtimeDao().insertAll(
                    new Showtime(1, 1, "2026-04-05 10:00", 90000),
                    new Showtime(1, 2, "2026-04-05 14:00", 85000),
                    new Showtime(2, 1, "2026-04-05 13:00", 80000),
                    new Showtime(2, 3, "2026-04-05 18:00", 95000),
                    new Showtime(3, 2, "2026-04-06 10:30", 90000),
                    new Showtime(3, 1, "2026-04-06 16:00", 100000),
                    new Showtime(4, 3, "2026-04-06 19:00", 95000),
                    new Showtime(5, 1, "2026-04-07 11:00", 85000),
                    new Showtime(5, 2, "2026-04-07 15:00", 90000),
                    new Showtime(5, 3, "2026-04-07 20:00", 100000)
                );

                // Seed Promotions (Mã khuyến mãi)
                database.promotionDao().insertAll(
                    new Promotion("SALE20", 20, "Giảm 20% cho tất cả vé", true),
                    new Promotion("NEWUSER", 15, "Giảm 15% cho người dùng mới", true),
                    new Promotion("MOVIE50", 50, "Giảm 50% - Ưu đãi đặc biệt", true),
                    new Promotion("WEEKEND", 10, "Giảm 10% vé cuối tuần", true)
                );
            });
        }
    }
}
