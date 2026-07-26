package com.example.midterm_application.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.Coffee;

import java.util.List;

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {
    private final List<Coffee> coffees;
    private final OnCoffeeClickListener clickListener;

    public CoffeeAdapter(List<Coffee> coffees, OnCoffeeClickListener clickListener) {
        this.coffees = coffees;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coffee_card, parent, false);
        return new CoffeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        Coffee coffee = coffees.get(position);
        holder.image.setImageResource(coffee.getImageResId());
        holder.name.setText(coffee.getName());
        holder.itemView.setOnClickListener(v -> clickListener.onCoffeeClicked(coffee));
    }

    @Override
    public int getItemCount() {
        return coffees.size();
    }

    public interface OnCoffeeClickListener {
        void onCoffeeClicked(Coffee coffee);
    }

    static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;

        CoffeeViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgCoffee);
            name = itemView.findViewById(R.id.tvCoffeeName);
        }
    }
}
