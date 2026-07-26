package com.example.midterm_application.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.local.AppDatabase;
import com.example.midterm_application.data.local.CartDao;
import com.example.midterm_application.data.model.CartItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private final CartDao cartDao;
    private final LiveData<List<CartItem>> cartItems;

    public CartRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        cartDao = database.cartDao();
        cartItems = cartDao.getAllCartItems();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public void insertCartItem(CartItem item) {
        databaseExecutor.execute(() -> cartDao.insert(item));
    }

    public void deleteCartItem(CartItem item) {
        databaseExecutor.execute(() -> cartDao.delete(item));
    }

    public void clearCart() {
        databaseExecutor.execute(cartDao::clearCart);
    }
}
