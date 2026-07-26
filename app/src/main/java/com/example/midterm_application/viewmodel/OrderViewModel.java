package com.example.midterm_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.midterm_application.data.repository.OrderRepository;

public class OrderViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final MutableLiveData<CheckoutState> checkoutState = new MutableLiveData<>(CheckoutState.idle());
    private volatile boolean checkoutInProgress;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderRepository(application);
    }

    public LiveData<CheckoutState> getCheckoutState() {
        return checkoutState;
    }

    public void checkout() {
        if (checkoutInProgress) {
            return;
        }

        checkoutInProgress = true;
        checkoutState.setValue(CheckoutState.loading());
        repository.checkout((orderId, errorMessage) -> {
            checkoutInProgress = false;
            if (errorMessage == null) {
                checkoutState.postValue(CheckoutState.success(orderId));
            } else {
                checkoutState.postValue(CheckoutState.error(errorMessage));
            }
        });
    }

    public void consumeCheckoutResult() {
        checkoutState.setValue(CheckoutState.idle());
    }

    public static class CheckoutState {
        private final boolean loading;
        private final boolean success;
        private final long orderId;
        private final String errorMessage;

        private CheckoutState(boolean loading, boolean success, long orderId, String errorMessage) {
            this.loading = loading;
            this.success = success;
            this.orderId = orderId;
            this.errorMessage = errorMessage;
        }

        public static CheckoutState idle() {
            return new CheckoutState(false, false, 0, null);
        }

        public static CheckoutState loading() {
            return new CheckoutState(true, false, 0, null);
        }

        public static CheckoutState success(long orderId) {
            return new CheckoutState(false, true, orderId, null);
        }

        public static CheckoutState error(String errorMessage) {
            return new CheckoutState(false, false, 0, errorMessage);
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
