package com.example.midterm_application.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

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
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public CartRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        cartDao = database.cartDao();
        cartItems = cartDao.getAllCartItems();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public void insertCartItem(CartItem item) {
        insertCartItem(item, null);
    }

    public void insertCartItem(CartItem item, InsertCartCallback callback) {
        databaseExecutor.execute(() -> {
            boolean successful = false;
            String errorMessage = null;
            try {
                long insertedId = cartDao.insert(item);
                successful = insertedId > 0L;
                if (!successful) {
                    errorMessage = "Could not add item to cart";
                }
            } catch (Exception exception) {
                errorMessage = "Could not add item to cart";
            }

            if (callback != null) {
                boolean result = successful;
                String message = errorMessage;
                mainHandler.post(() -> callback.onInsertComplete(result, message));
            }
        });
    }

    public void updateCartItem(CartItem item) {
        databaseExecutor.execute(() -> cartDao.update(item));
    }

    public void deleteCartItem(CartItem item) {
        databaseExecutor.execute(() -> cartDao.delete(item));
    }

    public void clearCart() {
        databaseExecutor.execute(cartDao::clearCart);
    }

    public interface InsertCartCallback {
        void onInsertComplete(boolean successful, String errorMessage);
    }
}
