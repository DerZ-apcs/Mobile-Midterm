package com.example.midterm_application;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.example.midterm_application.data.model.Coffee;
import com.example.midterm_application.data.repository.CoffeeRepository;
import com.example.midterm_application.utils.PriceCalculator.Ice;
import com.example.midterm_application.utils.PriceCalculator.Shot;
import com.example.midterm_application.utils.PriceCalculator.Size;
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
        setupCustomizationControls(coffee);
        refreshCustomizationUi(coffee);
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
        String formattedBasePrice = String.format(Locale.US, "$%.2f", coffee.getBasePrice());

        if (image != null) {
            image.setImageResource(coffee.getImageResId());
        }
        if (name != null) {
            name.setText(coffee.getName());
        }
        if (price != null) {
            price.setText(formattedBasePrice);
        }
    }

    private void setupCustomizationControls(Coffee coffee) {
        setClickListener(R.id.btnDecreaseQty, () -> {
            detailViewModel.setQuantity(detailViewModel.getQuantity() - 1);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnIncreaseQty, () -> {
            detailViewModel.setQuantity(detailViewModel.getQuantity() + 1);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnShotSingle, () -> {
            detailViewModel.setSelectedShot(Shot.SINGLE);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnShotDouble, () -> {
            detailViewModel.setSelectedShot(Shot.DOUBLE);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnSizeSmall, () -> {
            detailViewModel.setSelectedSize(Size.SMALL);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnSizeMedium, () -> {
            detailViewModel.setSelectedSize(Size.MEDIUM);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnSizeLarge, () -> {
            detailViewModel.setSelectedSize(Size.LARGE);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnIceNone, () -> {
            detailViewModel.setSelectedIce(Ice.NO_ICE);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnIceLight, () -> {
            detailViewModel.setSelectedIce(Ice.LESS_ICE);
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnIceFull, () -> {
            detailViewModel.setSelectedIce(Ice.NORMAL);
            refreshCustomizationUi(coffee);
        });
    }

    private void refreshCustomizationUi(Coffee coffee) {
        TextView totalAmount = findViewById(R.id.tvTotalAmount);
        TextView quantity = findViewById(R.id.tvQuantity);
        String formattedTotal = String.format(Locale.US, "$%.2f",
                detailViewModel.calculateTotal(coffee.getBasePrice()));

        if (totalAmount != null) {
            totalAmount.setText(formattedTotal);
        }
        if (quantity != null) {
            quantity.setText(String.valueOf(detailViewModel.getQuantity()));
        }

        updateShotOption(R.id.btnShotSingle, detailViewModel.getSelectedShot() == Shot.SINGLE);
        updateShotOption(R.id.btnShotDouble, detailViewModel.getSelectedShot() == Shot.DOUBLE);
        updateImageOption(R.id.btnSizeSmall, detailViewModel.getSelectedSize() == Size.SMALL);
        updateImageOption(R.id.btnSizeMedium, detailViewModel.getSelectedSize() == Size.MEDIUM);
        updateImageOption(R.id.btnSizeLarge, detailViewModel.getSelectedSize() == Size.LARGE);
        updateImageOption(R.id.btnIceNone, detailViewModel.getSelectedIce() == Ice.NO_ICE);
        updateImageOption(R.id.btnIceLight, detailViewModel.getSelectedIce() == Ice.LESS_ICE);
        updateImageOption(R.id.btnIceFull, detailViewModel.getSelectedIce() == Ice.NORMAL);
    }

    private void updateShotOption(int viewId, boolean selected) {
        TextView option = findViewById(viewId);
        if (option == null) {
            return;
        }

        option.setBackgroundResource(selected ? R.drawable.bg_segment_active : 0);
        option.setTextColor(getColor(selected ? R.color.white : R.color.gray_500));
    }

    private void updateImageOption(int viewId, boolean selected) {
        ImageButton option = findViewById(viewId);
        if (option == null) {
            return;
        }

        option.setBackgroundResource(selected ? R.drawable.bg_option_active : R.drawable.bg_option_inactive);
        option.setImageTintList(ColorStateList.valueOf(getColor(selected ? R.color.brand_dark : R.color.gray_400)));
    }

    private void setClickListener(int viewId, ClickAction action) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> action.run());
        }
    }

    private interface ClickAction {
        void run();
    }
}
