package com.example.midterm_application.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.RewardProduct;

import java.util.List;

public class RewardProductAdapter extends RecyclerView.Adapter<RewardProductAdapter.RewardProductViewHolder> {
    private final List<RewardProduct> rewards;
    private final OnRewardRedeemClickListener clickListener;
    private boolean redeemEnabled = true;

    public RewardProductAdapter(List<RewardProduct> rewards, OnRewardRedeemClickListener clickListener) {
        this.rewards = rewards;
        this.clickListener = clickListener;
    }

    public void setRedeemEnabled(boolean redeemEnabled) {
        this.redeemEnabled = redeemEnabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RewardProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_redeem_product, parent, false);
        return new RewardProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardProductViewHolder holder, int position) {
        RewardProduct reward = rewards.get(position);
        holder.image.setImageResource(reward.getImageResId());
        holder.name.setText(reward.getName());
        holder.pointCost.setText(holder.itemView.getContext()
                .getString(R.string.redeem_cost_format, reward.getPointCost()));
        holder.redeemAction.setText(R.string.cta_redeem);
        holder.redeemAction.setEnabled(redeemEnabled);
        holder.redeemAction.setAlpha(redeemEnabled ? 1.00f : 0.55f);
        holder.redeemAction.setOnClickListener(v -> {
            if (redeemEnabled) {
                clickListener.onRewardRedeemClicked(reward);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    public interface OnRewardRedeemClickListener {
        void onRewardRedeemClicked(RewardProduct rewardProduct);
    }

    static class RewardProductViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;
        final TextView pointCost;
        final TextView redeemAction;

        RewardProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgRedeemItem);
            name = itemView.findViewById(R.id.tvRedeemItemName);
            pointCost = itemView.findViewById(R.id.tvRedeemPointCost);
            redeemAction = itemView.findViewById(R.id.btnRedeemPoints);
        }
    }
}
