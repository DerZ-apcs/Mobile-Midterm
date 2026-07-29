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
import com.example.midterm_application.data.model.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<CartItem> items = new ArrayList<>();
    private final OnEditClickListener editClickListener;

    public CartAdapter(OnEditClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }

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
        bindNote(holder.note, item.getNote());
        holder.edit.setVisibility(View.VISIBLE);
        holder.edit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatVariant(CartItem item) {
        String variant = formatOption(item.getShot()) + " | "
                + formatOption(item.getSize()) + " | "
                + formatOption(item.getIce());
        if (item.isRewardItem()) {
            return variant + "\nReward - " + formatRewardSource(item.getRewardSource());
        }
        return variant;
    }

    private String formatRewardSource(String rewardSource) {
        if (CartItem.REWARD_SOURCE_STAMP_CARD.equals(rewardSource)) {
            return "Stamp Card";
        }
        if (CartItem.REWARD_SOURCE_POINTS.equals(rewardSource)) {
            return "Points";
        }
        return "Reward";
    }

    private String formatOption(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        String normalized = value.toLowerCase(Locale.US).replace('_', ' ');
        return normalized.substring(0, 1).toUpperCase(Locale.US) + normalized.substring(1);
    }

    private void bindNote(TextView noteView, String note) {
        if (note == null || note.trim().isEmpty()) {
            noteView.setVisibility(View.GONE);
            return;
        }
        noteView.setText(noteView.getContext().getString(R.string.order_note_label_format, note.trim()));
        noteView.setVisibility(View.VISIBLE);
    }

    public interface OnEditClickListener {
        void onEditClicked(CartItem item);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final ImageButton edit;
        final TextView name;
        final TextView variant;
        final TextView quantity;
        final TextView price;
        final TextView note;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgCartItem);
            edit = itemView.findViewById(R.id.btnEditCartItem);
            name = itemView.findViewById(R.id.tvCartItemName);
            variant = itemView.findViewById(R.id.tvCartItemVariant);
            quantity = itemView.findViewById(R.id.tvCartItemQty);
            price = itemView.findViewById(R.id.tvCartItemPrice);
            note = itemView.findViewById(R.id.tvCartItemNote);
        }
    }
}
