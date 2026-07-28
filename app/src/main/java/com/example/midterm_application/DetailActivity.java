package com.example.midterm_application;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm_application.data.model.Coffee;
import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.repository.CoffeeRepository;
import com.example.midterm_application.data.repository.ThemeRepository;
import com.example.midterm_application.utils.PriceCalculator.Ice;
import com.example.midterm_application.utils.PriceCalculator.Shot;
import com.example.midterm_application.utils.PriceCalculator.Size;
import com.example.midterm_application.utils.PriceCalculator;
import com.example.midterm_application.viewmodel.CartViewModel;
import com.example.midterm_application.viewmodel.DetailViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_COFFEE_ID = "com.example.midterm_application.EXTRA_COFFEE_ID";

    private DetailViewModel detailViewModel;
    private CartViewModel cartViewModel;
    private boolean hotSelected = true;
    private boolean addToCartInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedThemeMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_details);
        applyTopSystemBarInset(R.id.detailsRoot);
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        cartViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(CartViewModel.class);

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
        setupNoteInput();
        setupCustomizationControls(coffee);
        refreshCustomizationUi(coffee);
        cartViewModel.getCartItems().observe(this, this::updateCartBadge);
        setClickListener(R.id.btnCart, this::showCartPreview);
        setClickListener(R.id.btnAddToCart, () -> addCurrentCoffeeToCart(coffee));
    }

    private void applySavedThemeMode() {
        boolean darkModeEnabled = new ThemeRepository(getApplication()).isDarkModeEnabled();
        AppCompatDelegate.setDefaultNightMode(darkModeEnabled
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void applyTopSystemBarInset(int rootViewId) {
        View root = findViewById(rootViewId);
        if (root == null) {
            return;
        }

        int initialLeft = root.getPaddingLeft();
        int initialTop = root.getPaddingTop();
        int initialRight = root.getPaddingRight();
        int initialBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(initialLeft,
                    initialTop + systemBars.top,
                    initialRight,
                    initialBottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    private void setupNoteInput() {
        EditText noteInput = findViewById(R.id.etOrderNote);
        if (noteInput == null) {
            return;
        }
        noteInput.setText(detailViewModel.getNote());
        noteInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                detailViewModel.setNote(s == null ? "" : s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
        setClickListener(R.id.btnHot, () -> {
            hotSelected = true;
            refreshCustomizationUi(coffee);
        });
        setClickListener(R.id.btnCold, () -> {
            hotSelected = false;
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
        updateImageOption(R.id.btnHot, hotSelected);
        updateImageOption(R.id.btnCold, !hotSelected);
        updateImageOption(R.id.btnSizeSmall, detailViewModel.getSelectedSize() == Size.SMALL);
        updateImageOption(R.id.btnSizeMedium, detailViewModel.getSelectedSize() == Size.MEDIUM);
        updateImageOption(R.id.btnSizeLarge, detailViewModel.getSelectedSize() == Size.LARGE);
        updateIceOption(R.id.btnIceNone, detailViewModel.getSelectedIce() == Ice.NO_ICE);
        updateIceOption(R.id.btnIceLight, detailViewModel.getSelectedIce() == Ice.LESS_ICE);
        updateIceOption(R.id.btnIceFull, detailViewModel.getSelectedIce() == Ice.NORMAL);
    }

    private void addCurrentCoffeeToCart(Coffee coffee) {
        if (addToCartInProgress) {
            return;
        }

        double unitPrice = PriceCalculator.calculateTotal(
                coffee.getBasePrice(),
                detailViewModel.getSelectedShot(),
                detailViewModel.getSelectedSize(),
                detailViewModel.getSelectedIce(),
                1);
        double totalPrice = detailViewModel.calculateTotal(coffee.getBasePrice());
        CartItem cartItem = new CartItem(
                coffee.getId(),
                coffee.getName(),
                coffee.getImageResId(),
                detailViewModel.getSelectedShot().name(),
                detailViewModel.getSelectedSize().name(),
                detailViewModel.getSelectedIce().name(),
                detailViewModel.getQuantity(),
                unitPrice,
                totalPrice,
                detailViewModel.getNote());

        addToCartInProgress = true;
        setAddToCartEnabled(false);
        cartViewModel.insertCartItem(cartItem, (successful, errorMessage) -> {
            addToCartInProgress = false;
            setAddToCartEnabled(true);
            if (successful) {
                showAddedToCartSnackbar(coffee.getName());
            } else {
                showCartInsertError(errorMessage);
            }
        });
    }

    private void setAddToCartEnabled(boolean enabled) {
        View addToCart = findViewById(R.id.btnAddToCart);
        if (addToCart != null) {
            addToCart.setEnabled(enabled);
            addToCart.setAlpha(enabled ? 1.00f : 0.55f);
        }
    }

    private void showAddedToCartSnackbar(String coffeeName) {
        View root = findViewById(R.id.detailsRoot);
        if (root == null) {
            return;
        }

        Snackbar snackbar = Snackbar.make(root,
                getString(R.string.cart_added_snackbar_format, coffeeName),
                Snackbar.LENGTH_LONG);
        View addToCart = findViewById(R.id.btnAddToCart);
        if (addToCart != null) {
            snackbar.setAnchorView(addToCart);
        }
        snackbar.setAction(R.string.cta_view_cart, v -> openCartFromDetails());
        snackbar.show();
    }

    private void showCartInsertError(String errorMessage) {
        View root = findViewById(R.id.detailsRoot);
        if (root == null) {
            return;
        }

        Snackbar snackbar = Snackbar.make(root,
                errorMessage == null ? getString(R.string.cart_add_failed_message) : errorMessage,
                Snackbar.LENGTH_SHORT);
        View addToCart = findViewById(R.id.btnAddToCart);
        if (addToCart != null) {
            snackbar.setAnchorView(addToCart);
        }
        snackbar.show();
    }

    private void openCartFromDetails() {
        Intent result = new Intent();
        result.putExtra(MainActivity.EXTRA_OPEN_CART, true);
        setResult(RESULT_OK, result);
        finish();
    }

    private void showCartPreview() {
        TextView content = new TextView(this);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        content.setPadding(padding, padding, padding, padding);
        content.setTextColor(getColor(R.color.text_body));
        content.setTextSize(16);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(content);
        scrollView.addView(wrapper);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.cart_preview_title)
                .setView(scrollView)
                .setPositiveButton(R.string.cart_preview_close, null)
                .create();

        Observer<List<CartItem>> observer = items -> content.setText(formatCartPreview(items));
        dialog.setOnShowListener(dialogInterface -> cartViewModel.getCartItems().observe(this, observer));
        dialog.setOnDismissListener(dialogInterface -> cartViewModel.getCartItems().removeObserver(observer));
        dialog.show();
    }

    private String formatCartPreview(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return getString(R.string.cart_empty_message);
        }

        double total = 0.00;
        StringBuilder builder = new StringBuilder();
        builder.append("Items: ").append(items.size()).append("\n\n");
        for (CartItem item : items) {
            total += item.getTotalPrice();
            builder.append(item.getCoffeeName())
                    .append("  x")
                    .append(item.getQuantity());
            if (item.getNote() != null && !item.getNote().trim().isEmpty()) {
                builder.append("\n")
                        .append(getString(R.string.order_note_label_format, item.getNote().trim()));
            }
            builder.append("\n");
        }
        builder.append("\nTotal: ").append(String.format(Locale.US, "$%.2f", total));
        return builder.toString();
    }

    private void updateCartBadge(List<CartItem> items) {
        int badgeCount = 0;
        if (items != null) {
            for (CartItem item : items) {
                badgeCount += item.getQuantity();
            }
        }
        TextView badge = findViewById(R.id.tvCartBadge);
        if (badge != null) {
            badge.setText(String.valueOf(badgeCount));
            badge.setVisibility(badgeCount > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void updateShotOption(int viewId, boolean selected) {
        TextView option = findViewById(viewId);
        if (option == null) {
            return;
        }

        option.setBackgroundResource(selected ? R.drawable.bg_segment_active : R.drawable.bg_details_pill_inactive);
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

    private void updateIceOption(int viewId, boolean selected) {
        ImageButton option = findViewById(viewId);
        if (option == null) {
            return;
        }

        option.setBackgroundResource(selected ? R.drawable.bg_option_active : R.drawable.bg_option_inactive);
        option.setImageTintList(null);
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
