package com.example.midterm_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.repository.CartRepository;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository repository;
    private final LiveData<List<CartItem>> cartItems;

    public CartViewModel(@NonNull Application application) {
        super(application);
        repository = new CartRepository(application);
        cartItems = repository.getCartItems();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public void insertCartItem(CartItem item) {
        repository.insertCartItem(item);
    }

    public void updateCartItem(CartItem item) {
        repository.updateCartItem(item);
    }

    public void deleteCartItem(CartItem item) {
        repository.deleteCartItem(item);
    }

    public void clearCart() {
        repository.clearCart();
    }
}
