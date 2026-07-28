package com.example.midterm_application.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<OrderListItem> orders = new ArrayList<>();
    private final OnCompleteClickListener completeClickListener;
    private final OnReorderClickListener reorderClickListener;
    private final OnReviewClickListener reviewClickListener;

    public OrderAdapter(OnCompleteClickListener completeClickListener,
                        OnReorderClickListener reorderClickListener,
                        OnReviewClickListener reviewClickListener) {
        this.completeClickListener = completeClickListener;
        this.reorderClickListener = reorderClickListener;
        this.reviewClickListener = reviewClickListener;
    }

    public void submitOrders(List<OrderListItem> orderItems) {
        orders.clear();
        if (orderItems != null) {
            orders.addAll(orderItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderListItem order = orders.get(position);
        holder.date.setText(formatDate(order.getCreatedAt()));
        holder.name.setText(String.format(Locale.US, "Order #%d", order.getId()));
        holder.price.setText(String.format(Locale.US, "$%.2f", order.getTotalPrice()));
        holder.details.setText(formatDetails(order));

        boolean canComplete = Order.STATUS_ONGOING.equals(order.getStatus());
        boolean canReorder = Order.STATUS_COMPLETED.equals(order.getStatus());
        holder.complete.setVisibility(canComplete || canReorder ? View.VISIBLE : View.GONE);
        bindReview(holder, order, canReorder);
        if (canComplete) {
            holder.complete.setText(R.string.cta_complete_order);
            holder.complete.setOnClickListener(v -> completeClickListener.onCompleteClicked(order));
        } else if (canReorder) {
            holder.complete.setText(R.string.cta_order_again);
            holder.complete.setOnClickListener(v -> reorderClickListener.onReorderClicked(order));
        } else {
            holder.complete.setOnClickListener(null);
        }
    }

    private void bindReview(OrderViewHolder holder, OrderListItem order, boolean canReview) {
        holder.review.setVisibility(canReview ? View.VISIBLE : View.GONE);
        holder.review.setOnClickListener(canReview ? v -> reviewClickListener.onReviewClicked(order) : null);
        if (!canReview) {
            holder.reviewSummary.setVisibility(View.GONE);
            return;
        }

        holder.review.setText(order.hasReview() ? R.string.cta_edit_review : R.string.cta_rate_order);
        if (!order.hasReview()) {
            holder.reviewSummary.setVisibility(View.GONE);
            return;
        }

        String comment = order.getReviewComment() == null ? "" : order.getReviewComment().trim();
        holder.reviewSummary.setText(comment.isEmpty()
                ? holder.itemView.getContext().getString(R.string.review_summary_no_comment_format,
                formatStars(order.getReviewRating()))
                : holder.itemView.getContext().getString(R.string.review_summary_format,
                formatStars(order.getReviewRating()), comment));
        holder.reviewSummary.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String formatDate(long createdAt) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM | h:mm a", Locale.US);
        return formatter.format(new Date(createdAt));
    }

    private String formatDetails(OrderListItem order) {
        String summary = order.getItemSummary();
        if (summary == null || summary.isEmpty()) {
            summary = "No item summary";
        }
        return order.getStatus() + " · " + formatDeliveryTime(order) + "\n" + summary;
    }

    private String formatDeliveryTime(OrderListItem order) {
        if (Order.DELIVERY_SCHEDULED.equals(order.getDeliveryType()) && order.getScheduledAt() > 0L) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d · h:mm a", Locale.US);
            return "Scheduled for " + formatter.format(new Date(order.getScheduledAt()));
        }
        return "ASAP";
    }

    private String formatStars(int rating) {
        int safeRating = Math.max(0, Math.min(5, rating));
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < 5; index++) {
            builder.append(index < safeRating ? '\u2605' : '\u2606');
        }
        return builder.toString();
    }

    public interface OnCompleteClickListener {
        void onCompleteClicked(OrderListItem order);
    }

    public interface OnReorderClickListener {
        void onReorderClicked(OrderListItem order);
    }

    public interface OnReviewClickListener {
        void onReviewClicked(OrderListItem order);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final TextView date;
        final TextView name;
        final TextView price;
        final TextView details;
        final TextView complete;
        final TextView review;
        final TextView reviewSummary;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tvOrderDate);
            name = itemView.findViewById(R.id.tvOrderName);
            price = itemView.findViewById(R.id.tvOrderPrice);
            details = itemView.findViewById(R.id.tvOrderAddress);
            complete = itemView.findViewById(R.id.btnCompleteOrder);
            review = itemView.findViewById(R.id.btnReviewOrder);
            reviewSummary = itemView.findViewById(R.id.tvOrderReviewSummary);
        }
    }
}
