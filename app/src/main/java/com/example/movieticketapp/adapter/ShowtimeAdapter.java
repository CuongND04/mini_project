package com.example.movieticketapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.R;
import com.example.movieticketapp.database.Movie;
import com.example.movieticketapp.database.Showtime;
import com.example.movieticketapp.database.Theater;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ViewHolder> {

    private final List<Showtime> showtimes;
    private final Map<Integer, Movie> movieMap;
    private final Map<Integer, Theater> theaterMap;
    private final OnShowtimeClickListener listener;

    public interface OnShowtimeClickListener {
        void onBookClick(Showtime showtime);
    }

    public ShowtimeAdapter(List<Showtime> showtimes, Map<Integer, Movie> movieMap,
                           Map<Integer, Theater> theaterMap, OnShowtimeClickListener listener) {
        this.showtimes = showtimes;
        this.movieMap = movieMap;
        this.theaterMap = theaterMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_showtime, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Showtime showtime = showtimes.get(position);
        Movie movie = movieMap.get(showtime.movieId);
        Theater theater = theaterMap.get(showtime.theaterId);

        holder.tvMovieTitle.setText(movie != null ? movie.title : "Unknown");
        holder.tvTheaterName.setText(theater != null ? theater.name : "Unknown");
        holder.tvDateTime.setText(showtime.dateTime);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(nf.format(showtime.price) + " VND");

        holder.btnBook.setOnClickListener(v -> listener.onBookClick(showtime));
    }

    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvTheaterName, tvDateTime, tvPrice, btnBook;

        ViewHolder(View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvTheaterName = itemView.findViewById(R.id.tvTheaterName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
