package com.example.midterm_application;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.example.midterm_application.data.model.Coffee;
import com.example.midterm_application.data.repository.CoffeeRepository;
import com.example.midterm_application.viewmodel.DetailViewModel;

import java.util.Locale;

public class DetailActivity extends Activity {
    public static final String EXTRA_COFFEE_ID = "com.example.midterm_application.EXTRA_COFFEE_ID";

    private ViewModelStore viewModelStore;
    private DetailViewModel detailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_details);
        detailViewModel = getDetailViewModel();

        ImageButton backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        int coffeeId = getIntent().getIntExtra(EXTRA_COFFEE_ID, -1);
        Coffee coffee = CoffeeRepository.getCoffeeById(coffeeId);
        if (coffee == null) {
            finish();
            return;
        }

        bindCoffee(coffee);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return viewModelStore;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing() && viewModelStore != null) {
            viewModelStore.clear();
        }
    }

    private DetailViewModel getDetailViewModel() {
        Object lastInstance = getLastNonConfigurationInstance();
        if (lastInstance instanceof ViewModelStore) {
            viewModelStore = (ViewModelStore) lastInstance;
        } else {
            viewModelStore = new ViewModelStore();
        }
        return new ViewModelProvider(viewModelStore, new ViewModelProvider.NewInstanceFactory())
                .get(DetailViewModel.class);
    }

    private void bindCoffee(Coffee coffee) {
        ImageView image = findViewById(R.id.imgProduct);
        TextView name = findViewById(R.id.tvCoffeeName);
        TextView price = findViewById(R.id.tvCoffeePrice);
        TextView totalAmount = findViewById(R.id.tvTotalAmount);
        TextView quantity = findViewById(R.id.tvQuantity);
        String formattedBasePrice = String.format(Locale.US, "$%.2f", coffee.getBasePrice());
        String formattedTotal = String.format(Locale.US, "$%.2f",
                detailViewModel.calculateTotal(coffee.getBasePrice()));

        if (image != null) {
            image.setImageResource(coffee.getImageResId());
        }
        if (name != null) {
            name.setText(coffee.getName());
        }
        if (price != null) {
            price.setText(formattedBasePrice);
        }
        if (totalAmount != null) {
            totalAmount.setText(formattedTotal);
        }
        if (quantity != null) {
            quantity.setText(String.valueOf(detailViewModel.getQuantity()));
        }
    }
}
