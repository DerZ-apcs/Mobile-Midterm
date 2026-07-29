package com.example.midterm_application.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ItemViewHolder> {
    private final List<OrderItem> items = new ArrayList<>();

    public void submitItems(List<OrderItem> orderItems) {
        items.clear();
        if (orderItems != null) {
            items.addAll(orderItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.image.setImageResource(item.getImageResId());
        holder.name.setText(holder.itemView.getContext().getString(
                R.string.order_detail_item_name_quantity_format,
                item.getCoffeeName(), item.getQuantity()));
        holder.price.setText(String.format(Locale.US, "$%.2f", item.getTotalPrice()));
        holder.customization.setText(formatCustomization(item));
        bindNote(holder.note, item.getNote());
        bindReward(holder, item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void bindReward(ItemViewHolder holder, OrderItem item) {
        if (!item.isRewardItem()) {
            holder.rewardLabel.setVisibility(View.GONE);
            holder.rewardDescription.setVisibility(View.GONE);
            return;
        }

        holder.rewardLabel.setText(formatRewardSource(holder, item.getRewardSource()));
        holder.rewardLabel.setVisibility(View.VISIBLE);
        holder.rewardDescription.setVisibility(View.VISIBLE);
    }

    private String formatRewardSource(ItemViewHolder holder, String rewardSource) {
        if (OrderItem.REWARD_SOURCE_STAMP_CARD.equals(rewardSource)) {
            return holder.itemView.getContext().getString(R.string.order_detail_reward_loyalty);
        }
        if (OrderItem.REWARD_SOURCE_POINTS.equals(rewardSource)) {
            return holder.itemView.getContext().getString(R.string.order_detail_reward_points);
        }
        return holder.itemView.getContext().getString(R.string.order_detail_reward_generic);
    }

    private String formatCustomization(OrderItem item) {
        return formatOption(item.getShot()) + " · "
                + formatOption(item.getSize()) + " · "
                + formatOption(item.getIce());
    }

    private String formatOption(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String normalized = value.toLowerCase(Locale.US).replace('_', ' ');
        return normalized.substring(0, 1).toUpperCase(Locale.US) + normalized.substring(1);
    }

    private void bindNote(TextView noteView, String note) {
        String safeNote = note == null ? "" : note.trim();
        if (safeNote.isEmpty()) {
            noteView.setVisibility(View.GONE);
            return;
        }
        noteView.setText(noteView.getContext().getString(R.string.order_note_label_format, safeNote));
        noteView.setVisibility(View.VISIBLE);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;
        final TextView price;
        final TextView rewardLabel;
        final TextView customization;
        final TextView rewardDescription;
        final TextView note;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgOrderDetailItem);
            name = itemView.findViewById(R.id.tvOrderDetailItemName);
            price = itemView.findViewById(R.id.tvOrderDetailItemPrice);
            rewardLabel = itemView.findViewById(R.id.tvOrderDetailRewardLabel);
            customization = itemView.findViewById(R.id.tvOrderDetailItemCustomization);
            rewardDescription = itemView.findViewById(R.id.tvOrderDetailRewardDescription);
            note = itemView.findViewById(R.id.tvOrderDetailItemNote);
        }
    }
}
