package com.example.movieticketapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.movieticketapp.R;
import com.example.movieticketapp.database.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final List<Movie> movies;
    private final OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onViewShowtimes(Movie movie);
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(List<Movie> movies, OnMovieClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.tvTitle.setText(movie.title);
        holder.tvGenre.setText(movie.genre);
        holder.tvDuration.setText("⏱ " + movie.duration + " phút");
        holder.tvDescription.setText(movie.description);
        holder.btnViewShowtimes.setOnClickListener(v -> listener.onViewShowtimes(movie));

        // Click vào card -> Chi tiết phim
        holder.itemView.setOnClickListener(v -> listener.onMovieClick(movie));

        // Load poster image
        if (movie.imageUrl != null && !movie.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(movie.imageUrl)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .placeholder(R.drawable.bg_poster_placeholder)
                    .error(R.drawable.bg_poster_placeholder)
                    .into(holder.ivPoster);
        } else {
            holder.ivPoster.setImageResource(0);
            holder.ivPoster.setBackgroundResource(R.drawable.bg_poster_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvGenre, tvDuration, tvDescription, btnViewShowtimes;

        ViewHolder(View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnViewShowtimes = itemView.findViewById(R.id.btnViewShowtimes);
        }
    }
}
