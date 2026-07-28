package com.example.midterm_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.midterm_application.data.model.OrderReview;
import com.example.midterm_application.data.repository.OrderReviewRepository;

public class OrderReviewViewModel extends AndroidViewModel {
    private final OrderReviewRepository repository;
    private final MutableLiveData<ReviewSubmissionState> submissionState = new MutableLiveData<>();
    private boolean submissionInProgress;

    public OrderReviewViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderReviewRepository(application);
    }

    public LiveData<OrderReview> getReviewForOrder(long orderId) {
        return repository.getReviewForOrder(orderId);
    }

    public LiveData<ReviewSubmissionState> getSubmissionState() {
        return submissionState;
    }

    public void submitReview(long orderId, int rating, String comment) {
        if (submissionInProgress) {
            return;
        }

        submissionInProgress = true;
        submissionState.setValue(ReviewSubmissionState.loading(orderId));
        repository.submitReview(orderId, rating, comment, result -> {
            submissionInProgress = false;
            if (result.isSuccessful()) {
                submissionState.setValue(ReviewSubmissionState.success(result.getOrderId()));
            } else {
                submissionState.setValue(ReviewSubmissionState.error(result.getOrderId(), result.getErrorMessage()));
            }
        });
    }

    public void consumeSubmissionState() {
        submissionState.setValue(null);
    }

    public static class ReviewSubmissionState {
        private final boolean loading;
        private final boolean success;
        private final long orderId;
        private final String errorMessage;

        private ReviewSubmissionState(boolean loading, boolean success, long orderId, String errorMessage) {
            this.loading = loading;
            this.success = success;
            this.orderId = orderId;
            this.errorMessage = errorMessage;
        }

        public static ReviewSubmissionState loading(long orderId) {
            return new ReviewSubmissionState(true, false, orderId, null);
        }

        public static ReviewSubmissionState success(long orderId) {
            return new ReviewSubmissionState(false, true, orderId, null);
        }

        public static ReviewSubmissionState error(long orderId, String errorMessage) {
            return new ReviewSubmissionState(false, false, orderId, errorMessage);
        }

        public boolean isLoading() {
            return loading;
        }

        public boolean isSuccess() {
            return success;
        }

        public long getOrderId() {
            return orderId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
