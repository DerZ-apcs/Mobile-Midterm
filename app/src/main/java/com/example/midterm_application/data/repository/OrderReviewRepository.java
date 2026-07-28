package com.example.midterm_application.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.local.AppDatabase;
import com.example.midterm_application.data.local.OrderDao;
import com.example.midterm_application.data.local.OrderReviewDao;
import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderReview;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderReviewRepository {
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private final AppDatabase database;
    private final OrderDao orderDao;
    private final OrderReviewDao reviewDao;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public OrderReviewRepository(Application application) {
        database = AppDatabase.getInstance(application);
        orderDao = database.orderDao();
        reviewDao = database.orderReviewDao();
    }

    public LiveData<OrderReview> getReviewForOrder(long orderId) {
        return reviewDao.getReviewForOrder(orderId);
    }

    public void submitReview(long orderId, int rating, String rawComment, SubmitReviewCallback callback) {
        databaseExecutor.execute(() -> {
            SubmitReviewResult result;
            try {
                result = database.runInTransaction(new Callable<SubmitReviewResult>() {
                    @Override
                    public SubmitReviewResult call() {
                        if (!OrderReview.isValidRating(rating)) {
                            return SubmitReviewResult.failure(orderId, "Choose a rating from 1 to 5 stars");
                        }

                        Order order = orderDao.getOrderByIdSync(orderId);
                        if (order == null) {
                            return SubmitReviewResult.failure(orderId, "Order not found");
                        }
                        if (!Order.STATUS_COMPLETED.equals(order.getStatus())) {
                            return SubmitReviewResult.failure(orderId, "Only completed orders can be reviewed");
                        }

                        long now = System.currentTimeMillis();
                        String comment = rawComment == null ? "" : rawComment.trim();
                        OrderReview existingReview = reviewDao.getReviewForOrderSync(orderId);
                        if (existingReview == null) {
                            long reviewId = reviewDao.insert(new OrderReview(orderId, rating, comment, now, now));
                            if (reviewId <= 0L) {
                                return SubmitReviewResult.failure(orderId, "Could not save review");
                            }
                        } else {
                            existingReview.setRating(rating);
                            existingReview.setComment(comment);
                            existingReview.setUpdatedAt(now);
                            if (reviewDao.update(existingReview) != 1) {
                                return SubmitReviewResult.failure(orderId, "Could not update review");
                            }
                        }
                        return SubmitReviewResult.success(orderId);
                    }
                });
            } catch (Exception exception) {
                result = SubmitReviewResult.failure(orderId, "Could not save review");
            }

            if (callback != null) {
                SubmitReviewResult callbackResult = result;
                mainHandler.post(() -> callback.onSubmitReviewComplete(callbackResult));
            }
        });
    }

    public interface SubmitReviewCallback {
        void onSubmitReviewComplete(SubmitReviewResult result);
    }

    public static class SubmitReviewResult {
        private final boolean successful;
        private final long orderId;
        private final String errorMessage;

        private SubmitReviewResult(boolean successful, long orderId, String errorMessage) {
            this.successful = successful;
            this.orderId = orderId;
            this.errorMessage = errorMessage;
        }

        public static SubmitReviewResult success(long orderId) {
            return new SubmitReviewResult(true, orderId, null);
        }

        public static SubmitReviewResult failure(long orderId, String errorMessage) {
            return new SubmitReviewResult(false, orderId, errorMessage);
        }

        public boolean isSuccessful() {
            return successful;
        }

        public long getOrderId() {
            return orderId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
