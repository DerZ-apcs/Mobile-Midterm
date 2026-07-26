package com.example.midterm_application.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.midterm_application.data.model.CartItem;

import java.util.List;

@Dao
public interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    LiveData<List<CartItem>> getAllCartItems();

    @Insert
    void insert(CartItem item);

    @Delete
    void delete(CartItem item);

    @Query("DELETE FROM cart_items")
    void clearCart();
}
