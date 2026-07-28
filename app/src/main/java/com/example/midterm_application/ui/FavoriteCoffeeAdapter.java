package com.example.midterm_application.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.Coffee;

import java.util.ArrayList;
import java.util.List;

public class FavoriteCoffeeAdapter extends RecyclerView.Adapter<FavoriteCoffeeAdapter.FavoriteCoffeeViewHolder> {
    private final List<Coffee> coffees = new ArrayList<>();
    private final OnCoffeeClickListener clickListener;
    private final OnFavoriteClickListener favoriteClickListener;

    public FavoriteCoffeeAdapter(List<Coffee> coffees,
                                 OnCoffeeClickListener clickListener,
                                 OnFavoriteClickListener favoriteClickListener) {
        this.clickListener = clickListener;
        this.favoriteClickListener = favoriteClickListener;
        submitCoffees(coffees);
    }

    public void submitCoffees(List<Coffee> coffees) {
        this.coffees.clear();
        if (coffees != null) {
            this.coffees.addAll(coffees);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteCoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_coffee, parent, false);
        return new FavoriteCoffeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCoffeeViewHolder holder, int position) {
        Coffee coffee = coffees.get(position);
        holder.image.setImageResource(coffee.getImageResId());
        holder.name.setText(coffee.getName());
        holder.favorite.setOnClickListener(v -> favoriteClickListener.onFavoriteClicked(coffee));
        holder.itemView.setOnClickListener(v -> clickListener.onCoffeeClicked(coffee));
    }

    @Override
    public int getItemCount() {
        return coffees.size();
    }

    public interface OnCoffeeClickListener {
        void onCoffeeClicked(Coffee coffee);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClicked(Coffee coffee);
    }

    static class FavoriteCoffeeViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final ImageButton favorite;
        final TextView name;

        FavoriteCoffeeViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgFavoriteCoffee);
            favorite = itemView.findViewById(R.id.btnFavoriteCoffee);
            name = itemView.findViewById(R.id.tvFavoriteCoffeeName);
        }
    }
}
