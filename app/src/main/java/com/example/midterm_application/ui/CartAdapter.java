package com.example.midterm_application.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<CartItem> items = new ArrayList<>();

    public void submitItems(List<CartItem> cartItems) {
        items.clear();
        if (cartItems != null) {
            items.addAll(cartItems);
        }
        notifyDataSetChanged();
    }

    public CartItem getItemAt(int position) {
        if (position < 0 || position >= items.size()) {
            return null;
        }
        return items.get(position);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.image.setImageResource(item.getImageResId());
        holder.name.setText(item.getCoffeeName());
        holder.variant.setText(formatVariant(item));
        holder.quantity.setText(String.format(Locale.US, "x %d", item.getQuantity()));
        holder.price.setText(String.format(Locale.US, "$%.2f", item.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatVariant(CartItem item) {
        return formatOption(item.getShot()) + " | "
                + formatOption(item.getSize()) + " | "
                + formatOption(item.getIce());
    }

    private String formatOption(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        String normalized = value.toLowerCase(Locale.US).replace('_', ' ');
        return normalized.substring(0, 1).toUpperCase(Locale.US) + normalized.substring(1);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;
        final TextView variant;
        final TextView quantity;
        final TextView price;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgCartItem);
            name = itemView.findViewById(R.id.tvCartItemName);
            variant = itemView.findViewById(R.id.tvCartItemVariant);
            quantity = itemView.findViewById(R.id.tvCartItemQty);
            price = itemView.findViewById(R.id.tvCartItemPrice);
        }
    }
}
