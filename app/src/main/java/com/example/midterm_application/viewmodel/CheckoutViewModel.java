package com.example.midterm_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.CheckoutSummary;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.UserProfile;
import com.example.midterm_application.data.repository.CheckoutRepository;
import com.example.midterm_application.utils.RewardCalculator;

import java.util.ArrayList;
import java.util.List;

public class CheckoutViewModel extends AndroidViewModel {
    private final CheckoutRepository repository;
    private final MediatorLiveData<CheckoutSummary> summary = new MediatorLiveData<>();
    private final MutableLiveData<String> deliveryAddress = new MutableLiveData<>("");
    private final MutableLiveData<String> promoCode = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> loyaltyRequested = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loyaltyAvailable = new MutableLiveData<>(false);
    private final MutableLiveData<PlaceOrderState> placeOrderState = new MutableLiveData<>(PlaceOrderState.idle());

    private List<CartItem> latestCartItems = new ArrayList<>();
    private RewardState latestRewardState;
    private boolean placeOrderInProgress;

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        repository = new CheckoutRepository(application);

        UserProfile profile = repository.getProfile();
        deliveryAddress.setValue(profile == null ? "" : profile.getAddress());

        summary.addSource(repository.getCartItems(), items -> {
            latestCartItems = items == null ? new ArrayList<>() : new ArrayList<>(items);
            recalculateSummary();
        });
        summary.addSource(repository.getRewardState(), state -> {
            latestRewardState = state;
            boolean available = state != null && RewardCalculator.canClaimStampCard(state.getStampCount());
            loyaltyAvailable.setValue(available);
            if (!available && Boolean.TRUE.equals(loyaltyRequested.getValue())) {
                loyaltyRequested.setValue(false);
            }
            recalculateSummary();
        });
        recalculateSummary();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return repository.getCartItems();
    }

    public LiveData<CheckoutSummary> getSummary() {
        return summary;
    }

    public LiveData<String> getDeliveryAddress() {
        return deliveryAddress;
    }

    public LiveData<String> getPromoCode() {
        return promoCode;
    }

    public LiveData<Boolean> getLoyaltyRequested() {
        return loyaltyRequested;
    }

    public LiveData<Boolean> getLoyaltyAvailable() {
        return loyaltyAvailable;
    }

    public LiveData<PlaceOrderState> getPlaceOrderState() {
        return placeOrderState;
    }

    public void setDeliveryAddress(String address) {
        deliveryAddress.setValue(address == null ? "" : address);
    }

    public void setPromoCode(String code) {
        promoCode.setValue(code == null ? "" : code);
        recalculateSummary();
    }

    public void setLoyaltyRequested(boolean requested) {
        boolean available = Boolean.TRUE.equals(loyaltyAvailable.getValue());
        loyaltyRequested.setValue(requested && available);
        recalculateSummary();
    }

    public void placeOrder() {
        if (placeOrderInProgress) {
            return;
        }
        placeOrderInProgress = true;
        placeOrderState.setValue(PlaceOrderState.loading());
        repository.placeOrder(
                deliveryAddress.getValue(),
                promoCode.getValue(),
                Boolean.TRUE.equals(loyaltyRequested.getValue()),
                result -> {
                    placeOrderInProgress = false;
                    if (result.isSuccessful()) {
                        placeOrderState.postValue(PlaceOrderState.success(result.getOrderId()));
                    } else {
                        placeOrderState.postValue(PlaceOrderState.error(result.getErrorMessage()));
                    }
                });
    }

    public void consumePlaceOrderResult() {
        placeOrderState.setValue(PlaceOrderState.idle());
    }

    private void recalculateSummary() {
        CheckoutSummary checkoutSummary = repository.calculateSummary(
                latestCartItems,
                latestRewardState,
                promoCode.getValue(),
                Boolean.TRUE.equals(loyaltyRequested.getValue()));
        summary.setValue(checkoutSummary);
    }

    public static class PlaceOrderState {
        private final boolean loading;
        private final boolean success;
        private final long orderId;
        private final String errorMessage;

        private PlaceOrderState(boolean loading, boolean success, long orderId, String errorMessage) {
            this.loading = loading;
            this.success = success;
            this.orderId = orderId;
            this.errorMessage = errorMessage;
        }

        public static PlaceOrderState idle() {
            return new PlaceOrderState(false, false, 0, null);
        }

        public static PlaceOrderState loading() {
            return new PlaceOrderState(true, false, 0, null);
        }

        public static PlaceOrderState success(long orderId) {
            return new PlaceOrderState(false, true, orderId, null);
        }

        public static PlaceOrderState error(String errorMessage) {
            return new PlaceOrderState(false, false, 0, errorMessage);
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
