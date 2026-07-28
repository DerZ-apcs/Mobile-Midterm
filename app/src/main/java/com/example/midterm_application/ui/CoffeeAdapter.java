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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {
    private final List<Coffee> coffees = new ArrayList<>();
    private final OnCoffeeClickListener clickListener;
    private final OnFavoriteClickListener favoriteClickListener;
    private Set<Integer> favoriteCoffeeIds = new HashSet<>();

    public CoffeeAdapter(List<Coffee> coffees, Set<Integer> favoriteCoffeeIds,
                         OnCoffeeClickListener clickListener,
                         OnFavoriteClickListener favoriteClickListener) {
        this.clickListener = clickListener;
        this.favoriteClickListener = favoriteClickListener;
        submitCoffees(coffees);
        setFavoriteCoffeeIds(favoriteCoffeeIds);
    }

    public void submitCoffees(List<Coffee> coffees) {
        this.coffees.clear();
        if (coffees != null) {
            this.coffees.addAll(coffees);
        }
        notifyDataSetChanged();
    }

    public void setFavoriteCoffeeIds(Set<Integer> favoriteCoffeeIds) {
        this.favoriteCoffeeIds = favoriteCoffeeIds == null ? new HashSet<>() : new HashSet<>(favoriteCoffeeIds);
        notifyDataSetChanged();
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
        boolean favorite = favoriteCoffeeIds.contains(coffee.getId());
        holder.image.setImageResource(coffee.getImageResId());
        holder.name.setText(coffee.getName());
        holder.favorite.setImageResource(favorite
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
        holder.favorite.setColorFilter(holder.itemView.getContext().getColor(
                favorite ? R.color.icon_on_light : R.color.text_secondary_on_light));
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

    static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final ImageButton favorite;
        final TextView name;

        CoffeeViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgCoffee);
            favorite = itemView.findViewById(R.id.btnFavoriteCoffee);
            name = itemView.findViewById(R.id.tvCoffeeName);
        }
    }
}
