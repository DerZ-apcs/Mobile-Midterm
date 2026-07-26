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

    public OrderAdapter(OnCompleteClickListener completeClickListener) {
        this.completeClickListener = completeClickListener;
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
        holder.complete.setVisibility(canComplete ? View.VISIBLE : View.GONE);
        holder.complete.setOnClickListener(canComplete
                ? v -> completeClickListener.onCompleteClicked(order)
                : null);
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
        return order.getStatus() + "\n" + summary;
    }

    public interface OnCompleteClickListener {
        void onCompleteClicked(OrderListItem order);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final TextView date;
        final TextView name;
        final TextView price;
        final TextView details;
        final TextView complete;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tvOrderDate);
            name = itemView.findViewById(R.id.tvOrderName);
            price = itemView.findViewById(R.id.tvOrderPrice);
            details = itemView.findViewById(R.id.tvOrderAddress);
            complete = itemView.findViewById(R.id.btnCompleteOrder);
        }
    }
}
