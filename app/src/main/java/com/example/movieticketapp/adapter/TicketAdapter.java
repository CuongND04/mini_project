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
import com.example.movieticketapp.database.Ticket;

import java.util.Map;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private final List<Ticket> tickets;
    private final Map<Integer, Showtime> showtimeMap;
    private final Map<Integer, Movie> movieMap;
    private final Map<Integer, Theater> theaterMap;

    public TicketAdapter(List<Ticket> tickets, Map<Integer, Showtime> showtimeMap,
                         Map<Integer, Movie> movieMap, Map<Integer, Theater> theaterMap) {
        this.tickets = tickets;
        this.showtimeMap = showtimeMap;
        this.movieMap = movieMap;
        this.theaterMap = theaterMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        Showtime showtime = showtimeMap.get(ticket.showtimeId);
        Movie movie = showtime != null ? movieMap.get(showtime.movieId) : null;
        Theater theater = showtime != null ? theaterMap.get(showtime.theaterId) : null;

        holder.tvMovieTitle.setText(movie != null ? movie.title : "Unknown");
        holder.tvTheaterName.setText(theater != null ? theater.name : "");
        holder.tvDateTime.setText(showtime != null ? showtime.dateTime : "");
        holder.tvSeat.setText(ticket.seatNumber);
        holder.tvBookingTime.setText("Đặt lúc: " + ticket.bookingTime);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvTheaterName, tvDateTime, tvSeat, tvBookingTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvTheaterName = itemView.findViewById(R.id.tvTheaterName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvSeat = itemView.findViewById(R.id.tvSeat);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
        }
    }
}
