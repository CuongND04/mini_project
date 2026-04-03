package com.example.movieticketapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {

    private final List<String> allSeats;
    private final List<String> bookedSeats;
    private final Set<String> selectedSeats = new HashSet<>();
    private final OnSeatClickListener listener;

    public interface OnSeatClickListener {
        void onSeatSelectionChanged(Set<String> selectedSeats);
    }

    public SeatAdapter(List<String> allSeats, List<String> bookedSeats, OnSeatClickListener listener) {
        this.allSeats = allSeats;
        this.bookedSeats = bookedSeats;
        this.listener = listener;
    }

    public Set<String> getSelectedSeats() {
        return selectedSeats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String seat = allSeats.get(position);
        holder.tvSeat.setText(seat);

        if (bookedSeats.contains(seat)) {
            holder.tvSeat.setBackgroundResource(R.drawable.seat_booked);
            holder.tvSeat.setEnabled(false);
            holder.tvSeat.setAlpha(0.5f);
        } else if (selectedSeats.contains(seat)) {
            holder.tvSeat.setBackgroundResource(R.drawable.seat_selected);
            holder.tvSeat.setEnabled(true);
            holder.tvSeat.setAlpha(1.0f);
        } else {
            holder.tvSeat.setBackgroundResource(R.drawable.seat_available);
            holder.tvSeat.setEnabled(true);
            holder.tvSeat.setAlpha(1.0f);
        }

        holder.tvSeat.setOnClickListener(v -> {
            if (!bookedSeats.contains(seat)) {
                if (selectedSeats.contains(seat)) {
                    selectedSeats.remove(seat);
                } else {
                    selectedSeats.add(seat);
                }
                notifyDataSetChanged();
                listener.onSeatSelectionChanged(selectedSeats);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allSeats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeat;

        ViewHolder(View itemView) {
            super(itemView);
            tvSeat = itemView.findViewById(R.id.tvSeat);
        }
    }
}
