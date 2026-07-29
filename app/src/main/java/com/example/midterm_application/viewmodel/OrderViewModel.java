package com.example.midterm_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.midterm_application.data.model.OrderDetails;
import com.example.midterm_application.data.model.OrderListItem;
import com.example.midterm_application.data.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final MutableLiveData<CheckoutState> checkoutState = new MutableLiveData<>(CheckoutState.idle());
    private final MutableLiveData<ReorderState> reorderState = new MutableLiveData<>();
    private final MutableLiveData<OrderDetailsState> orderDetailsState = new MutableLiveData<>();
    private volatile boolean checkoutInProgress;
    private volatile boolean reorderInProgress;
    private volatile boolean orderDetailsInProgress;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderRepository(application);
    }

    public LiveData<CheckoutState> getCheckoutState() {
        return checkoutState;
    }

    public LiveData<ReorderState> getReorderState() {
        return reorderState;
    }

    public LiveData<OrderDetailsState> getOrderDetailsState() {
        return orderDetailsState;
    }

    public LiveData<List<OrderListItem>> getOngoingOrders() {
        return repository.getOngoingOrders();
    }

    public LiveData<List<OrderListItem>> getCompletedOrders() {
        return repository.getCompletedOrders();
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

    public void completeOrder(long orderId) {
        repository.markOrderCompleted(orderId);
    }

    public void reorderCompletedOrder(long orderId) {
        if (reorderInProgress) {
            return;
        }

        reorderInProgress = true;
        repository.reorderCompletedOrder(orderId, (addedItems, errorMessage) -> {
            reorderInProgress = false;
            if (errorMessage == null) {
                reorderState.postValue(ReorderState.success(addedItems));
            } else {
                reorderState.postValue(ReorderState.error(errorMessage));
            }
        });
    }

    public void consumeReorderResult() {
        reorderState.setValue(null);
    }

    public void loadOrderDetails(long orderId) {
        if (orderDetailsInProgress) {
            return;
        }

        orderDetailsInProgress = true;
        orderDetailsState.setValue(OrderDetailsState.loading());
        repository.getOrderDetails(orderId, result -> {
            orderDetailsInProgress = false;
            if (result.isSuccessful()) {
                orderDetailsState.postValue(OrderDetailsState.success(result.getDetails()));
            } else {
                orderDetailsState.postValue(OrderDetailsState.error(result.getErrorMessage()));
            }
        });
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

    public static class ReorderState {
        private final boolean success;
        private final int addedItems;
        private final String errorMessage;

        private ReorderState(boolean success, int addedItems, String errorMessage) {
            this.success = success;
            this.addedItems = addedItems;
            this.errorMessage = errorMessage;
        }

        public static ReorderState success(int addedItems) {
            return new ReorderState(true, addedItems, null);
        }

        public static ReorderState error(String errorMessage) {
            return new ReorderState(false, 0, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public int getAddedItems() {
            return addedItems;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class OrderDetailsState {
        private final boolean loading;
        private final OrderDetails details;
        private final String errorMessage;

        private OrderDetailsState(boolean loading, OrderDetails details, String errorMessage) {
            this.loading = loading;
            this.details = details;
            this.errorMessage = errorMessage;
        }

        public static OrderDetailsState loading() {
            return new OrderDetailsState(true, null, null);
        }

        public static OrderDetailsState success(OrderDetails details) {
            return new OrderDetailsState(false, details, null);
        }

        public static OrderDetailsState error(String errorMessage) {
            return new OrderDetailsState(false, null, errorMessage);
        }

        public boolean isLoading() {
            return loading;
        }

        public OrderDetails getDetails() {
            return details;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
