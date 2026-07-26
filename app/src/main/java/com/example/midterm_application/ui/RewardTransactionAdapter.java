package com.example.midterm_application.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.RewardTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RewardTransactionAdapter extends RecyclerView.Adapter<RewardTransactionAdapter.RewardViewHolder> {
    private final List<RewardTransaction> transactions = new ArrayList<>();

    public void submitTransactions(List<RewardTransaction> rewardTransactions) {
        transactions.clear();
        if (rewardTransactions != null) {
            transactions.addAll(rewardTransactions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reward_history, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        RewardTransaction transaction = transactions.get(position);
        holder.description.setText(transaction.getDescription());
        holder.date.setText(formatDate(transaction.getCreatedAt()));
        holder.points.setText(String.format(Locale.US, "+ %d Pts", transaction.getPoints()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    private String formatDate(long createdAt) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM | h:mm a", Locale.US);
        return formatter.format(new Date(createdAt));
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        final TextView description;
        final TextView date;
        final TextView points;

        RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.tvHistoryDrinkName);
            date = itemView.findViewById(R.id.tvHistoryDate);
            points = itemView.findViewById(R.id.tvHistoryPoints);
        }
    }
}
