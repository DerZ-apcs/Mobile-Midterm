package com.example.midterm_application;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.midterm_application.data.model.Coffee;
import com.example.midterm_application.data.repository.CoffeeRepository;

import java.util.Locale;

public class DetailActivity extends Activity {
    public static final String EXTRA_COFFEE_ID = "com.example.midterm_application.EXTRA_COFFEE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_details);

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

    private void bindCoffee(Coffee coffee) {
        ImageView image = findViewById(R.id.imgProduct);
        TextView name = findViewById(R.id.tvCoffeeName);
        TextView price = findViewById(R.id.tvCoffeePrice);
        TextView totalAmount = findViewById(R.id.tvTotalAmount);
        String formattedPrice = String.format(Locale.US, "$%.2f", coffee.getBasePrice());

        if (image != null) {
            image.setImageResource(coffee.getImageResId());
        }
        if (name != null) {
            name.setText(coffee.getName());
        }
        if (price != null) {
            price.setText(formattedPrice);
        }
        if (totalAmount != null) {
            totalAmount.setText(formattedPrice);
        }
    }
}
