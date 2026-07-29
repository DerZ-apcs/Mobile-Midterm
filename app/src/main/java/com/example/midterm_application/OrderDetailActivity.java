package com.example.midterm_application;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderDetails;
import com.example.midterm_application.data.model.OrderReview;
import com.example.midterm_application.data.repository.ThemeRepository;
import com.example.midterm_application.ui.OrderDetailItemAdapter;
import com.example.midterm_application.viewmodel.OrderViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ORDER_ID = "com.example.midterm_application.EXTRA_ORDER_ID";

    private OrderViewModel orderViewModel;
    private OrderDetailItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedThemeMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        applyTopSystemBarInset(R.id.orderDetailRoot);

        ImageButton backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        itemAdapter = new OrderDetailItemAdapter();
        RecyclerView itemList = findViewById(R.id.rvOrderDetailItems);
        if (itemList != null) {
            itemList.setLayoutManager(new LinearLayoutManager(this));
            itemList.setNestedScrollingEnabled(false);
            itemList.setAdapter(itemAdapter);
        }

        orderViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(OrderViewModel.class);
        orderViewModel.getOrderDetailsState().observe(this, this::renderState);

        long orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, -1L);
        if (orderId <= 0L) {
            Toast.makeText(this, R.string.order_detail_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        orderViewModel.loadOrderDetails(orderId);
    }

    private void applySavedThemeMode() {
        boolean darkModeEnabled = new ThemeRepository(getApplication()).isDarkModeEnabled();
        AppCompatDelegate.setDefaultNightMode(darkModeEnabled
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void applyTopSystemBarInset(int rootViewId) {
        View root = findViewById(rootViewId);
        if (root == null) {
            return;
        }

        int initialLeft = root.getPaddingLeft();
        int initialTop = root.getPaddingTop();
        int initialRight = root.getPaddingRight();
        int initialBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(initialLeft,
                    initialTop + systemBars.top,
                    initialRight,
                    initialBottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    private void renderState(OrderViewModel.OrderDetailsState state) {
        if (state == null) {
            return;
        }

        setVisibility(R.id.tvOrderDetailLoading, state.isLoading() ? View.VISIBLE : View.GONE);
        if (state.isLoading()) {
            return;
        }

        if (state.getDetails() == null) {
            Toast.makeText(this, getOrderDetailError(state), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindDetails(state.getDetails());
    }

    private String getOrderDetailError(OrderViewModel.OrderDetailsState state) {
        String error = state.getErrorMessage();
        return error == null || error.trim().isEmpty()
                ? getString(R.string.order_detail_load_failed)
                : error;
    }

    private void bindDetails(@NonNull OrderDetails details) {
        Order order = details.getOrder();
        if (order == null) {
            Toast.makeText(this, R.string.order_detail_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setText(R.id.tvOrderDetailOrderNumber,
                getString(R.string.order_detail_order_number_format, order.getId()));
        setText(R.id.tvOrderDetailStatus, order.getStatus());
        setText(R.id.tvOrderDetailDate, formatDateTime(order.getCreatedAt()));
        setText(R.id.tvOrderDetailDeliveryType, formatDelivery(order));
        bindAddress(order.getDeliveryAddress());
        itemAdapter.submitItems(details.getItems());
        bindPriceBreakdown(order);
        bindReview(order, details.getReview());
    }

    private void bindAddress(String address) {
        String safeAddress = address == null ? "" : address.trim();
        boolean hasAddress = !safeAddress.isEmpty();
        setVisibility(R.id.layoutOrderDetailAddress, hasAddress ? View.VISIBLE : View.GONE);
        if (hasAddress) {
            setText(R.id.tvOrderDetailAddress, safeAddress);
        }
    }

    private void bindPriceBreakdown(Order order) {
        double subtotal = order.getSubtotal() > 0.00 ? order.getSubtotal() : order.getTotalPrice();
        double finalTotal = order.getFinalTotal() > 0.00 || order.getTotalPrice() == 0.00
                ? order.getFinalTotal()
                : order.getTotalPrice();
        setText(R.id.tvOrderDetailSubtotal,
                getString(R.string.checkout_subtotal_format, subtotal));
        setText(R.id.tvOrderDetailFinalTotal,
                getString(R.string.checkout_final_total_format, finalTotal));

        boolean hasLoyaltyDiscount = order.getLoyaltyDiscount() > 0.00;
        setVisibility(R.id.tvOrderDetailLoyaltyDiscount, hasLoyaltyDiscount ? View.VISIBLE : View.GONE);
        if (hasLoyaltyDiscount) {
            setText(R.id.tvOrderDetailLoyaltyDiscount,
                    getString(R.string.checkout_loyalty_discount_format, order.getLoyaltyDiscount()));
        }

        String promoCode = order.getPromoCode() == null ? "" : order.getPromoCode().trim();
        boolean hasPromo = !promoCode.isEmpty() || order.getPromoDiscount() > 0.00;
        setVisibility(R.id.tvOrderDetailPromoDiscount, hasPromo ? View.VISIBLE : View.GONE);
        if (hasPromo) {
            setText(R.id.tvOrderDetailPromoDiscount,
                    getString(R.string.order_detail_promo_format,
                            promoCode.isEmpty() ? getString(R.string.checkout_promo_code) : promoCode,
                            order.getPromoDiscount()));
        }
    }

    private void bindReview(Order order, OrderReview review) {
        boolean showReview = Order.STATUS_COMPLETED.equals(order.getStatus())
                && review != null
                && OrderReview.isValidRating(review.getRating());
        setVisibility(R.id.layoutOrderDetailReview, showReview ? View.VISIBLE : View.GONE);
        if (!showReview) {
            return;
        }

        setText(R.id.tvOrderDetailReviewStars, formatStars(review.getRating()));
        String comment = review.getComment() == null ? "" : review.getComment().trim();
        setText(R.id.tvOrderDetailReviewComment,
                comment.isEmpty() ? getString(R.string.order_detail_no_review_comment) : comment);
    }

    private String formatDelivery(Order order) {
        if (Order.DELIVERY_SCHEDULED.equals(order.getDeliveryType()) && order.getScheduledAt() > 0L) {
            return getString(R.string.order_detail_scheduled_for_format,
                    formatDateTime(order.getScheduledAt()));
        }
        return getString(R.string.checkout_delivery_asap);
    }

    private String formatDateTime(long timestamp) {
        if (timestamp <= 0L) {
            return getString(R.string.order_detail_unavailable);
        }
        return new SimpleDateFormat("dd MMM yyyy | h:mm a", Locale.US).format(new Date(timestamp));
    }

    private String formatStars(int rating) {
        int safeRating = Math.max(0, Math.min(5, rating));
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < 5; index++) {
            builder.append(index < safeRating ? '\u2605' : '\u2606');
        }
        return builder.toString();
    }

    private void setText(int viewId, String value) {
        TextView view = findViewById(viewId);
        if (view != null) {
            view.setText(value == null ? "" : value);
        }
    }

    private void setVisibility(int viewId, int visibility) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }
}
