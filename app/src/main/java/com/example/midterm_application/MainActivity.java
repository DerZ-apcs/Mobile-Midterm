package com.example.midterm_application;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.CheckoutSummary;
import com.example.midterm_application.data.model.Coffee;
import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderListItem;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;
import com.example.midterm_application.data.model.UserProfile;
import com.example.midterm_application.data.repository.FavoriteRepository;
import com.example.midterm_application.data.repository.RewardCatalog;
import com.example.midterm_application.data.repository.RewardRepository;
import com.example.midterm_application.data.repository.CoffeeRepository;
import com.example.midterm_application.data.repository.ThemeRepository;
import com.example.midterm_application.ui.CartAdapter;
import com.example.midterm_application.ui.CoffeeAdapter;
import com.example.midterm_application.ui.FavoriteCoffeeAdapter;
import com.example.midterm_application.ui.OrderAdapter;
import com.example.midterm_application.ui.RewardProductAdapter;
import com.example.midterm_application.ui.RewardTransactionAdapter;
import com.example.midterm_application.utils.PriceCalculator;
import com.example.midterm_application.utils.PriceCalculator.Ice;
import com.example.midterm_application.utils.PriceCalculator.Shot;
import com.example.midterm_application.utils.PriceCalculator.Size;
import com.example.midterm_application.utils.RewardCalculator;
import com.example.midterm_application.viewmodel.CartViewModel;
import com.example.midterm_application.viewmodel.CheckoutViewModel;
import com.example.midterm_application.viewmodel.OrderViewModel;
import com.example.midterm_application.viewmodel.OrderReviewViewModel;
import com.example.midterm_application.viewmodel.ProfileViewModel;
import com.example.midterm_application.viewmodel.RewardViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_OPEN_CART = "com.example.midterm_application.EXTRA_OPEN_CART";

    private static final int REQUEST_COFFEE_DETAILS = 1001;
    private static final long BRANDED_SPLASH_VISIBLE_MS = 850L;
    private static final long BRANDED_SPLASH_FADE_MS = 300L;

    private static final int SCREEN_HOME = 1;
    private static final int SCREEN_DETAILS = 2;
    private static final int SCREEN_CART = 3;
    private static final int SCREEN_ORDER_SUCCESS = 4;
    private static final int SCREEN_ORDERS = 5;
    private static final int SCREEN_REWARDS = 6;
    private static final int SCREEN_REDEEM = 7;
    private static final int SCREEN_PROFILE = 8;
    private static final int SCREEN_CHECKOUT = 9;

    private static final String STATE_CURRENT_SCREEN = "current_screen";
    private static final String STATE_SELECTED_COFFEE_ID = "selected_coffee_id";
    private static final String STATE_SELECTED_COFFEE_NAME = "selected_coffee_name";
    private static final String STATE_BACK_STACK = "back_stack";
    private static final String STATE_SHOWING_HISTORY_ORDERS = "showing_history_orders";
    private static final String STATE_PROFILE_EDIT_MODE = "profile_edit_mode";
    private static final String STATE_COFFEE_SEARCH_QUERY = "coffee_search_query";

    private final ArrayList<Integer> backStack = new ArrayList<>();
    private int currentScreen = SCREEN_HOME;
    private int selectedCoffeeId = 1;
    private String selectedCoffeeName = "Americano";
    private CartViewModel cartViewModel;
    private CheckoutViewModel checkoutViewModel;
    private OrderViewModel orderViewModel;
    private OrderReviewViewModel orderReviewViewModel;
    private ProfileViewModel profileViewModel;
    private RewardViewModel rewardViewModel;
    private FavoriteRepository favoriteRepository;
    private ThemeRepository themeRepository;
    private List<CartItem> currentCartItems = new ArrayList<>();
    private boolean checkoutInProgress;
    private boolean showingHistoryOrders;
    private boolean profileEditMode;
    private String coffeeSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedThemeMode();
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        boolean shouldShowBrandedSplash = savedInstanceState == null;

        if (savedInstanceState != null) {
            currentScreen = savedInstanceState.getInt(STATE_CURRENT_SCREEN, SCREEN_HOME);
            selectedCoffeeId = savedInstanceState.getInt(STATE_SELECTED_COFFEE_ID, 1);
            selectedCoffeeName = savedInstanceState.getString(STATE_SELECTED_COFFEE_NAME, "Americano");
            showingHistoryOrders = savedInstanceState.getBoolean(STATE_SHOWING_HISTORY_ORDERS, false);
            profileEditMode = savedInstanceState.getBoolean(STATE_PROFILE_EDIT_MODE, false);
            coffeeSearchQuery = savedInstanceState.getString(STATE_COFFEE_SEARCH_QUERY, "");
            ArrayList<Integer> restoredStack = savedInstanceState.getIntegerArrayList(STATE_BACK_STACK);
            if (restoredStack != null) {
                backStack.addAll(restoredStack);
            }
        }

        setupBackNavigation();
        showScreen(currentScreen);
        if (shouldShowBrandedSplash) {
            showBrandedSplashOverlay();
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MainActivity.this.handleBackPressed();
            }
        });
    }

    private void showBrandedSplashOverlay() {
        ViewGroup content = findViewById(android.R.id.content);
        if (content == null) {
            return;
        }

        Window window = getWindow();
        View decorView = window.getDecorView();
        int originalStatusBarColor = window.getStatusBarColor();
        int originalNavigationBarColor = window.getNavigationBarColor();
        int originalSystemUiVisibility = decorView.getSystemUiVisibility();

        window.setStatusBarColor(getColor(R.color.splash_system_background));
        window.setNavigationBarColor(getColor(R.color.splash_system_background));
        int splashSystemUiVisibility = originalSystemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            splashSystemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        decorView.setSystemUiVisibility(splashSystemUiVisibility);

        View overlay = LayoutInflater.from(this).inflate(R.layout.view_branded_splash, content, false);
        content.addView(overlay, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        overlay.postDelayed(() -> overlay.animate()
                .alpha(0f)
                .setDuration(BRANDED_SPLASH_FADE_MS)
                .withEndAction(() -> {
                    content.removeView(overlay);
                    window.setStatusBarColor(originalStatusBarColor);
                    window.setNavigationBarColor(originalNavigationBarColor);
                    decorView.setSystemUiVisibility(originalSystemUiVisibility);
                })
                .start(), BRANDED_SPLASH_VISIBLE_MS);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_SCREEN, currentScreen);
        outState.putInt(STATE_SELECTED_COFFEE_ID, selectedCoffeeId);
        outState.putString(STATE_SELECTED_COFFEE_NAME, selectedCoffeeName);
        outState.putIntegerArrayList(STATE_BACK_STACK, backStack);
        outState.putBoolean(STATE_SHOWING_HISTORY_ORDERS, showingHistoryOrders);
        outState.putBoolean(STATE_PROFILE_EDIT_MODE, profileEditMode);
        outState.putString(STATE_COFFEE_SEARCH_QUERY, coffeeSearchQuery);
    }

    private void handleBackPressed() {
        if (currentScreen == SCREEN_PROFILE) {
            profileEditMode = false;
        }
        if (!backStack.isEmpty()) {
            int previousScreen = backStack.remove(backStack.size() - 1);
            showScreen(previousScreen);
            return;
        }

        if (currentScreen != SCREEN_HOME) {
            showScreen(SCREEN_HOME);
            return;
        }

        finish();
    }

    private void navigateTo(int screen) {
        if (currentScreen != screen) {
            backStack.add(currentScreen);
        }
        showScreen(screen);
    }

    private void navigateToPrimary(int screen) {
        if (!isPrimaryScreen(screen) || currentScreen == screen) {
            return;
        }

        backStack.clear();
        showScreen(screen);
    }

    private void navigateToDetails(int coffeeId, String coffeeName) {
        selectedCoffeeId = coffeeId;
        selectedCoffeeName = coffeeName;
        navigateTo(SCREEN_DETAILS);
    }

    private void openCoffeeDetails(int coffeeId) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_COFFEE_ID, coffeeId);
        startActivityForResult(intent, REQUEST_COFFEE_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COFFEE_DETAILS
                && resultCode == RESULT_OK
                && data != null
                && data.getBooleanExtra(EXTRA_OPEN_CART, false)) {
            navigateTo(SCREEN_CART);
        }
    }

    private void showScreen(int screen) {
        currentScreen = screen;

        switch (screen) {
            case SCREEN_DETAILS:
                showDetails();
                break;
            case SCREEN_CART:
                showCart();
                break;
            case SCREEN_ORDER_SUCCESS:
                showOrderSuccess();
                break;
            case SCREEN_CHECKOUT:
                showCheckout();
                break;
            case SCREEN_ORDERS:
                showOrders();
                break;
            case SCREEN_REWARDS:
                showRewards();
                break;
            case SCREEN_REDEEM:
                showRedeem();
                break;
            case SCREEN_PROFILE:
                showProfile();
                break;
            case SCREEN_HOME:
            default:
                showHome();
                break;
        }
    }

    private void showHome() {
        setContentView(R.layout.activity_home);

        EditText searchInput = findViewById(R.id.etCoffeeSearch);
        TextView emptyResults = findViewById(R.id.tvEmptyCoffeeResults);
        RecyclerView coffeeGrid = findViewById(R.id.rvCoffeeGrid);
        RecyclerView favoriteList = findViewById(R.id.rvFavoriteCoffees);
        View favoritesSection = findViewById(R.id.layoutFavoritesSection);
        Set<Integer> favoriteCoffeeIds = getFavoriteRepository().getFavoriteCoffeeIds();
        List<Coffee> displayedCatalogCoffees = getDisplayedCatalogCoffees();
        final CoffeeAdapter[] adapterRef = new CoffeeAdapter[1];
        final FavoriteCoffeeAdapter[] favoriteAdapterRef = new FavoriteCoffeeAdapter[1];
        CoffeeAdapter coffeeAdapter = new CoffeeAdapter(
                displayedCatalogCoffees,
                favoriteCoffeeIds,
                coffee -> openCoffeeDetails(coffee.getId()),
                coffee -> {
                    Set<Integer> updatedFavorites = getFavoriteRepository().toggleFavorite(coffee.getId());
                    adapterRef[0].setFavoriteCoffeeIds(updatedFavorites);
                    refreshFavoritesSection(favoriteAdapterRef[0], favoritesSection, updatedFavorites);
                });
        adapterRef[0] = coffeeAdapter;
        FavoriteCoffeeAdapter favoriteAdapter = new FavoriteCoffeeAdapter(
                resolveFavoriteCoffees(favoriteCoffeeIds),
                coffee -> openCoffeeDetails(coffee.getId()),
                coffee -> {
                    Set<Integer> updatedFavorites = getFavoriteRepository().toggleFavorite(coffee.getId());
                    adapterRef[0].setFavoriteCoffeeIds(updatedFavorites);
                    refreshFavoritesSection(favoriteAdapterRef[0], favoritesSection, updatedFavorites);
                });
        favoriteAdapterRef[0] = favoriteAdapter;
        if (coffeeGrid != null) {
            coffeeGrid.setLayoutManager(new GridLayoutManager(this, 2));
            coffeeGrid.setNestedScrollingEnabled(false);
            coffeeGrid.setAdapter(coffeeAdapter);
        }
        if (favoriteList != null) {
            favoriteList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            favoriteList.setAdapter(favoriteAdapter);
        }
        updateFavoritesSectionVisibility(favoritesSection, favoriteAdapter.getItemCount());
        updateCoffeeEmptyState(displayedCatalogCoffees, emptyResults);
        getProfileViewModel().getProfile().removeObservers(this);
        getProfileViewModel().getProfile().observe(this, this::updateHomeProfileName);
        getProfileViewModel().reloadProfile();
        if (searchInput != null) {
            searchInput.setText(coffeeSearchQuery);
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    coffeeSearchQuery = s == null ? "" : s.toString();
                    List<Coffee> filteredCoffees = getDisplayedCatalogCoffees();
                    coffeeAdapter.submitCoffees(filteredCoffees);
                    updateCoffeeEmptyState(filteredCoffees, emptyResults);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        getCartViewModel().getCartItems().removeObservers(this);
        getCartViewModel().getCartItems().observe(this, this::updateCartBadge);
        getRewardViewModel().getRewardState().removeObservers(this);
        getRewardViewModel().getRewardState().observe(this, this::updateHomeRewardState);
        setClickListener(R.id.btnCart, () -> navigateTo(SCREEN_CART));
        setClickListener(R.id.btnProfile, () -> navigateTo(SCREEN_PROFILE));
        setupPrimaryBottomNavigation(R.id.navHome);
    }

    private void updateHomeProfileName(UserProfile profile) {
        TextView userName = findViewById(R.id.tvUserName);
        if (userName == null || profile == null) {
            return;
        }

        String fullName = profile.getFullName();
        userName.setText(fullName == null ? "" : fullName.trim());
    }

    private List<Coffee> getDisplayedCatalogCoffees() {
        return new ArrayList<>(CoffeeRepository.searchByName(coffeeSearchQuery));
    }

    private void updateHomeRewardState(RewardState rewardState) {
        int stampCount = rewardState == null ? 0 : RewardCalculator.capStampCount(rewardState.getStampCount());
        updateLoyaltyStampCount(R.id.tvStampCount, stampCount);
        updateStampImages(stampCount);
    }

    private void updateCoffeeEmptyState(List<Coffee> coffees, TextView emptyResults) {
        if (emptyResults != null) {
            boolean isEmpty = coffees == null || coffees.isEmpty();
            emptyResults.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    private List<Coffee> resolveFavoriteCoffees(Set<Integer> favoriteCoffeeIds) {
        List<Coffee> favoriteCoffees = new ArrayList<>();
        if (favoriteCoffeeIds == null || favoriteCoffeeIds.isEmpty()) {
            return favoriteCoffees;
        }

        for (Coffee coffee : CoffeeRepository.getAllCoffees()) {
            if (favoriteCoffeeIds.contains(coffee.getId())) {
                favoriteCoffees.add(coffee);
            }
        }
        return favoriteCoffees;
    }

    private void refreshFavoritesSection(FavoriteCoffeeAdapter favoriteAdapter,
                                         View favoritesSection,
                                         Set<Integer> favoriteCoffeeIds) {
        List<Coffee> favoriteCoffees = resolveFavoriteCoffees(favoriteCoffeeIds);
        if (favoriteAdapter != null) {
            favoriteAdapter.submitCoffees(favoriteCoffees);
        }
        updateFavoritesSectionVisibility(favoritesSection, favoriteCoffees.size());
    }

    private void updateFavoritesSectionVisibility(View favoritesSection, int favoriteCount) {
        if (favoritesSection != null) {
            favoritesSection.setVisibility(favoriteCount > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void showDetails() {
        setContentView(R.layout.activity_coffee_details);

        TextView coffeeName = findViewById(R.id.tvCoffeeName);
        if (coffeeName != null) {
            coffeeName.setText(selectedCoffeeName);
        }

        setClickListener(R.id.btnBack, this::goBackOrHome);
        setClickListener(R.id.btnCart, () -> navigateTo(SCREEN_CART));
        setClickListener(R.id.btnAddToCart, () -> navigateTo(SCREEN_CART));
    }

    private void showCart() {
        setContentView(R.layout.activity_cart);

        setClickListener(R.id.btnBack, this::goBackOrHome);
        View checkout = findViewById(R.id.btnCheckout);
        if (checkout != null) {
            checkout.setOnClickListener(v -> navigateTo(SCREEN_CHECKOUT));
        }

        CartAdapter cartAdapter = new CartAdapter(this::showEditCartItemDialog);
        RecyclerView cartItems = findViewById(R.id.rvCartItems);
        if (cartItems != null) {
            cartItems.setLayoutManager(new LinearLayoutManager(this));
            cartItems.setAdapter(cartAdapter);
            attachSwipeToDelete(cartItems, cartAdapter);
        }

        getCartViewModel().getCartItems().removeObservers(this);
        getCartViewModel().getCartItems().observe(this, items -> {
            currentCartItems = items == null ? new ArrayList<>() : new ArrayList<>(items);
            cartAdapter.submitItems(items);
            updateCartSummary(items);
        });

        checkoutInProgress = false;
        updateCartSummary(currentCartItems);
    }

    private void showEditCartItemDialog(CartItem item) {
        if (item == null) {
            return;
        }

        Coffee coffee = CoffeeRepository.getCoffeeById(item.getCoffeeId());
        if (coffee == null) {
            Toast.makeText(this, R.string.cart_edit_missing_coffee, Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_cart_item, null, false);
        TextView name = dialogView.findViewById(R.id.tvEditCartItemName);
        TextView quantityText = dialogView.findViewById(R.id.tvEditQuantity);
        TextView totalText = dialogView.findViewById(R.id.tvEditCartTotal);
        TextView decreaseQuantity = dialogView.findViewById(R.id.btnEditDecreaseQty);
        TextView increaseQuantity = dialogView.findViewById(R.id.btnEditIncreaseQty);
        RadioGroup shotGroup = dialogView.findViewById(R.id.rgEditShot);
        RadioGroup sizeGroup = dialogView.findViewById(R.id.rgEditSize);
        RadioGroup iceGroup = dialogView.findViewById(R.id.rgEditIce);
        EditText noteInput = dialogView.findViewById(R.id.etEditOrderNote);

        final int[] quantity = {PriceCalculator.normalizeQuantity(item.getQuantity())};
        final Shot[] selectedShot = {parseShot(item.getShot())};
        final Size[] selectedSize = {parseSize(item.getSize())};
        final Ice[] selectedIce = {parseIce(item.getIce())};

        if (name != null) {
            name.setText(item.getCoffeeName());
        }
        if (noteInput != null) {
            noteInput.setText(item.getNote() == null ? "" : item.getNote());
        }

        checkShot(shotGroup, selectedShot[0]);
        checkSize(sizeGroup, selectedSize[0]);
        checkIce(iceGroup, selectedIce[0]);

        Runnable refreshPrice = () -> updateEditCartPrice(
                coffee.getBasePrice(), selectedShot[0], selectedSize[0], selectedIce[0], quantity[0],
                quantityText, totalText);
        refreshPrice.run();

        if (decreaseQuantity != null) {
            decreaseQuantity.setOnClickListener(v -> {
                quantity[0] = PriceCalculator.normalizeQuantity(quantity[0] - 1);
                refreshPrice.run();
            });
        }
        if (increaseQuantity != null) {
            increaseQuantity.setOnClickListener(v -> {
                quantity[0] = PriceCalculator.normalizeQuantity(quantity[0] + 1);
                refreshPrice.run();
            });
        }
        if (shotGroup != null) {
            shotGroup.setOnCheckedChangeListener((group, checkedId) -> {
                selectedShot[0] = checkedId == R.id.rbEditShotDouble ? Shot.DOUBLE : Shot.SINGLE;
                refreshPrice.run();
            });
        }
        if (sizeGroup != null) {
            sizeGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbEditSizeLarge) {
                    selectedSize[0] = Size.LARGE;
                } else if (checkedId == R.id.rbEditSizeMedium) {
                    selectedSize[0] = Size.MEDIUM;
                } else {
                    selectedSize[0] = Size.SMALL;
                }
                refreshPrice.run();
            });
        }
        if (iceGroup != null) {
            iceGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbEditIceNone) {
                    selectedIce[0] = Ice.NO_ICE;
                } else if (checkedId == R.id.rbEditIceLight) {
                    selectedIce[0] = Ice.LESS_ICE;
                } else {
                    selectedIce[0] = Ice.NORMAL;
                }
                refreshPrice.run();
            });
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_cart_item_title)
                .setView(dialogView)
                .setNegativeButton(R.string.cta_cancel_profile, null)
                .setPositiveButton(R.string.cta_save_profile, null)
                .create();
        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    CartItem updatedItem = copyEditedCartItem(item, coffee, selectedShot[0], selectedSize[0],
                            selectedIce[0], quantity[0], noteInput);
                    getCartViewModel().updateCartItem(updatedItem);
                    dialog.dismiss();
                }));
        dialog.show();
    }

    private void updateEditCartPrice(double basePrice, Shot shot, Size size, Ice ice, int quantity,
                                     TextView quantityText, TextView totalText) {
        double totalPrice = PriceCalculator.calculateTotal(basePrice, shot, size, ice, quantity);
        if (quantityText != null) {
            quantityText.setText(String.valueOf(quantity));
        }
        if (totalText != null) {
            totalText.setText(String.format(Locale.US, "$%.2f", totalPrice));
        }
    }

    private CartItem copyEditedCartItem(CartItem item, Coffee coffee, Shot shot, Size size, Ice ice,
                                        int quantity, EditText noteInput) {
        double unitPrice = PriceCalculator.calculateTotal(coffee.getBasePrice(), shot, size, ice, 1);
        double totalPrice = PriceCalculator.calculateTotal(coffee.getBasePrice(), shot, size, ice, quantity);
        CartItem updatedItem = new CartItem(
                item.getCoffeeId(),
                item.getCoffeeName(),
                item.getImageResId(),
                shot.name(),
                size.name(),
                ice.name(),
                quantity,
                unitPrice,
                totalPrice,
                noteInput == null ? "" : noteInput.getText().toString());
        updatedItem.setId(item.getId());
        return updatedItem;
    }

    private Shot parseShot(String value) {
        try {
            return Shot.valueOf(value == null ? "" : value);
        } catch (IllegalArgumentException ignored) {
            return Shot.SINGLE;
        }
    }

    private Size parseSize(String value) {
        try {
            return Size.valueOf(value == null ? "" : value);
        } catch (IllegalArgumentException ignored) {
            return Size.SMALL;
        }
    }

    private Ice parseIce(String value) {
        try {
            return Ice.valueOf(value == null ? "" : value);
        } catch (IllegalArgumentException ignored) {
            return Ice.NORMAL;
        }
    }

    private void checkShot(RadioGroup shotGroup, Shot shot) {
        if (shotGroup != null) {
            shotGroup.check(shot == Shot.DOUBLE ? R.id.rbEditShotDouble : R.id.rbEditShotSingle);
        }
    }

    private void checkSize(RadioGroup sizeGroup, Size size) {
        if (sizeGroup == null) {
            return;
        }
        if (size == Size.LARGE) {
            sizeGroup.check(R.id.rbEditSizeLarge);
        } else if (size == Size.MEDIUM) {
            sizeGroup.check(R.id.rbEditSizeMedium);
        } else {
            sizeGroup.check(R.id.rbEditSizeSmall);
        }
    }

    private void checkIce(RadioGroup iceGroup, Ice ice) {
        if (iceGroup == null) {
            return;
        }
        if (ice == Ice.NO_ICE) {
            iceGroup.check(R.id.rbEditIceNone);
        } else if (ice == Ice.LESS_ICE) {
            iceGroup.check(R.id.rbEditIceLight);
        } else {
            iceGroup.check(R.id.rbEditIceNormal);
        }
    }

    private void showCheckout() {
        setContentView(R.layout.activity_checkout);

        CheckoutViewModel checkoutViewModel = getCheckoutViewModel();
        EditText deliveryAddress = findViewById(R.id.etDeliveryAddress);
        EditText promoCode = findViewById(R.id.etPromoCode);
        CheckBox loyaltyReward = findViewById(R.id.cbUseLoyaltyReward);
        RadioGroup deliveryTimeGroup = findViewById(R.id.rgDeliveryTime);

        setClickListener(R.id.btnBack, this::goBackOrHome);
        setClickListener(R.id.btnApplyPromo, () -> {
            if (promoCode != null) {
                checkoutViewModel.setPromoCode(promoCode.getText().toString());
            }
        });
        setClickListener(R.id.btnPlaceOrder, checkoutViewModel::placeOrder);

        if (deliveryAddress != null) {
            deliveryAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkoutViewModel.setDeliveryAddress(s == null ? "" : s.toString());
                    updatePlaceOrderButton();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        if (promoCode != null) {
            promoCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkoutViewModel.setPromoCode(s == null ? "" : s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        if (loyaltyReward != null) {
            loyaltyReward.setOnCheckedChangeListener((buttonView, isChecked) ->
                    checkoutViewModel.setLoyaltyRequested(isChecked));
        }
        setupDeliveryTimeControls(checkoutViewModel, deliveryTimeGroup);

        checkoutViewModel.getDeliveryAddress().removeObservers(this);
        checkoutViewModel.getDeliveryAddress().observe(this, address -> {
            if (deliveryAddress != null && address != null
                    && !address.contentEquals(deliveryAddress.getText())) {
                deliveryAddress.setText(address);
            }
            updatePlaceOrderButton();
        });
        checkoutViewModel.getPromoCode().removeObservers(this);
        checkoutViewModel.getPromoCode().observe(this, code -> {
            if (promoCode != null && code != null && !code.contentEquals(promoCode.getText())) {
                promoCode.setText(code);
            }
        });
        checkoutViewModel.getLoyaltyAvailable().removeObservers(this);
        checkoutViewModel.getLoyaltyAvailable().observe(this, available -> {
            boolean enabled = Boolean.TRUE.equals(available);
            if (loyaltyReward != null) {
                loyaltyReward.setEnabled(enabled);
                loyaltyReward.setAlpha(enabled ? 1.00f : 0.45f);
            }
            TextView hint = findViewById(R.id.tvLoyaltyRewardHint);
            if (hint != null) {
                hint.setText(enabled
                        ? R.string.checkout_loyalty_available
                        : R.string.checkout_loyalty_unavailable);
            }
        });
        checkoutViewModel.getLoyaltyRequested().removeObservers(this);
        checkoutViewModel.getLoyaltyRequested().observe(this, requested -> {
            if (loyaltyReward != null
                    && loyaltyReward.isChecked() != Boolean.TRUE.equals(requested)) {
                loyaltyReward.setChecked(Boolean.TRUE.equals(requested));
            }
        });
        checkoutViewModel.getDeliveryType().removeObservers(this);
        checkoutViewModel.getDeliveryType().observe(this, type -> {
            updateDeliveryTypeSelection(deliveryTimeGroup, type);
            updateScheduledControlsVisibility(type);
            updatePlaceOrderButton();
        });
        checkoutViewModel.getScheduledAt().removeObservers(this);
        checkoutViewModel.getScheduledAt().observe(this, scheduledAt -> updateScheduledDateTimeLabels(
                scheduledAt == null ? 0L : scheduledAt));
        checkoutViewModel.getDeliveryScheduleValid().removeObservers(this);
        checkoutViewModel.getDeliveryScheduleValid().observe(this, valid -> updatePlaceOrderButton());
        checkoutViewModel.getDeliveryScheduleMessage().removeObservers(this);
        checkoutViewModel.getDeliveryScheduleMessage().observe(this, this::updateDeliveryTimeMessage);
        checkoutViewModel.getCartItems().removeObservers(this);
        checkoutViewModel.getCartItems().observe(this, items -> {
            currentCartItems = items == null ? new ArrayList<>() : new ArrayList<>(items);
            updatePlaceOrderButton();
        });
        checkoutViewModel.getSummary().removeObservers(this);
        checkoutViewModel.getSummary().observe(this, this::updateCheckoutSummary);
        checkoutViewModel.getPlaceOrderState().removeObservers(this);
        checkoutViewModel.getPlaceOrderState().observe(this, state -> {
            if (state == null) {
                return;
            }
            checkoutInProgress = state.isLoading();
            updatePlaceOrderButton();
            if (state.isSuccess()) {
                checkoutViewModel.consumePlaceOrderResult();
                navigateTo(SCREEN_ORDER_SUCCESS);
                return;
            }
            if (state.getErrorMessage() != null) {
                Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                checkoutViewModel.consumePlaceOrderResult();
            }
        });
    }

    private void setupDeliveryTimeControls(CheckoutViewModel checkoutViewModel, RadioGroup deliveryTimeGroup) {
        if (deliveryTimeGroup != null) {
            deliveryTimeGroup.setOnCheckedChangeListener((group, checkedId) -> checkoutViewModel.setDeliveryType(
                    checkedId == R.id.rbDeliveryScheduled ? Order.DELIVERY_SCHEDULED : Order.DELIVERY_ASAP));
        }

        TextView dateButton = findViewById(R.id.btnSelectDeliveryDate);
        if (dateButton != null) {
            dateButton.setOnClickListener(v -> showDeliveryDatePicker(checkoutViewModel));
        }

        TextView timeButton = findViewById(R.id.btnSelectDeliveryTime);
        if (timeButton != null) {
            timeButton.setOnClickListener(v -> showDeliveryTimePicker(checkoutViewModel));
        }
    }

    private void showDeliveryDatePicker(CheckoutViewModel checkoutViewModel) {
        Calendar calendar = getScheduledCalendar(checkoutViewModel);
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar updated = getScheduledCalendar(checkoutViewModel);
                    updated.set(Calendar.YEAR, year);
                    updated.set(Calendar.MONTH, month);
                    updated.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updated.set(Calendar.SECOND, 0);
                    updated.set(Calendar.MILLISECOND, 0);
                    checkoutViewModel.setScheduledAt(updated.getTimeInMillis());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void showDeliveryTimePicker(CheckoutViewModel checkoutViewModel) {
        Calendar calendar = getScheduledCalendar(checkoutViewModel);
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    Calendar updated = getScheduledCalendar(checkoutViewModel);
                    updated.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    updated.set(Calendar.MINUTE, minute);
                    updated.set(Calendar.SECOND, 0);
                    updated.set(Calendar.MILLISECOND, 0);
                    checkoutViewModel.setScheduledAt(updated.getTimeInMillis());
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);
        dialog.show();
    }

    private Calendar getScheduledCalendar(CheckoutViewModel checkoutViewModel) {
        long scheduledAt = checkoutViewModel.getScheduledAtValue();
        Calendar calendar = Calendar.getInstance();
        long fallback = System.currentTimeMillis() + CheckoutViewModel.MIN_SCHEDULE_DELAY_MS;
        calendar.setTimeInMillis(scheduledAt > 0L ? scheduledAt : fallback);
        return calendar;
    }

    private void updateDeliveryTypeSelection(RadioGroup deliveryTimeGroup, String deliveryType) {
        if (deliveryTimeGroup == null) {
            return;
        }
        int expectedId = Order.DELIVERY_SCHEDULED.equals(deliveryType)
                ? R.id.rbDeliveryScheduled
                : R.id.rbDeliveryAsap;
        if (deliveryTimeGroup.getCheckedRadioButtonId() != expectedId) {
            deliveryTimeGroup.check(expectedId);
        }
    }

    private void updateScheduledControlsVisibility(String deliveryType) {
        View scheduledControls = findViewById(R.id.layoutScheduledControls);
        if (scheduledControls != null) {
            scheduledControls.setVisibility(Order.DELIVERY_SCHEDULED.equals(deliveryType)
                    ? View.VISIBLE
                    : View.GONE);
        }
    }

    private void updateScheduledDateTimeLabels(long scheduledAt) {
        TextView dateButton = findViewById(R.id.btnSelectDeliveryDate);
        TextView timeButton = findViewById(R.id.btnSelectDeliveryTime);
        if (scheduledAt <= 0L) {
            if (dateButton != null) {
                dateButton.setText(R.string.checkout_select_date);
            }
            if (timeButton != null) {
                timeButton.setText(R.string.checkout_select_time);
            }
            return;
        }

        Date scheduledDate = new Date(scheduledAt);
        if (dateButton != null) {
            dateButton.setText(new SimpleDateFormat("MMM d", Locale.US).format(scheduledDate));
        }
        if (timeButton != null) {
            timeButton.setText(new SimpleDateFormat("h:mm a", Locale.US).format(scheduledDate));
        }
    }

    private void updateDeliveryTimeMessage(String message) {
        TextView messageView = findViewById(R.id.tvDeliveryTimeMessage);
        if (messageView == null) {
            return;
        }
        boolean hasMessage = message != null && !message.trim().isEmpty();
        messageView.setText(hasMessage ? message : "");
        messageView.setVisibility(hasMessage ? View.VISIBLE : View.GONE);
    }

    private void showOrderSuccess() {
        setContentView(R.layout.activity_order_success);

        setClickListener(R.id.btnTrackOrder, () -> {
            showingHistoryOrders = false;
            navigateToPrimary(SCREEN_ORDERS);
        });
    }

    private void showOrders() {
        setContentView(R.layout.activity_my_order);

        OrderAdapter orderAdapter = new OrderAdapter(
                order -> getOrderViewModel().completeOrder(order.getId()),
                order -> getOrderViewModel().reorderCompletedOrder(order.getId()),
                this::showOrderReviewDialog);
        RecyclerView orderList = findViewById(R.id.rvOrderList);
        if (orderList != null) {
            orderList.setLayoutManager(new LinearLayoutManager(this));
            orderList.setAdapter(orderAdapter);
        }

        TextView tabOngoing = findViewById(R.id.tabOngoing);
        TextView tabHistory = findViewById(R.id.tabHistory);
        View tabOngoingIndicator = findViewById(R.id.tabOngoingIndicator);
        View tabHistoryIndicator = findViewById(R.id.tabHistoryIndicator);
        TextView emptyOrders = findViewById(R.id.tvEmptyOrders);
        if (tabOngoing != null) {
            tabOngoing.setOnClickListener(v -> showOrderTab(false, orderAdapter, emptyOrders,
                    tabOngoing, tabHistory, tabOngoingIndicator, tabHistoryIndicator));
        }
        if (tabHistory != null) {
            tabHistory.setOnClickListener(v -> showOrderTab(true, orderAdapter, emptyOrders,
                    tabOngoing, tabHistory, tabOngoingIndicator, tabHistoryIndicator));
        }
        showOrderTab(showingHistoryOrders, orderAdapter, emptyOrders,
                tabOngoing, tabHistory, tabOngoingIndicator, tabHistoryIndicator);

        getOrderViewModel().getReorderState().removeObservers(this);
        getOrderViewModel().getReorderState().observe(this, this::showReorderResult);
        getOrderReviewViewModel().getSubmissionState().removeObservers(this);
        getOrderReviewViewModel().getSubmissionState().observe(this, this::showReviewSubmissionResult);

        setupPrimaryBottomNavigation(R.id.navOrders);
    }

    private void showOrderReviewDialog(OrderListItem order) {
        if (order == null || !Order.STATUS_COMPLETED.equals(order.getStatus())) {
            Toast.makeText(this, R.string.review_completed_only_message, Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_order_review, null, false);
        TextView title = dialogView.findViewById(R.id.tvReviewOrderTitle);
        RatingBar ratingBar = dialogView.findViewById(R.id.rbOrderReview);
        EditText commentInput = dialogView.findViewById(R.id.etOrderReviewComment);

        if (title != null) {
            title.setText(getString(R.string.review_order_title_format, order.getId()));
        }
        if (ratingBar != null) {
            ratingBar.setRating(order.hasReview() ? order.getReviewRating() : 0);
        }
        if (commentInput != null) {
            commentInput.setText(order.getReviewComment() == null ? "" : order.getReviewComment());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(order.hasReview() ? R.string.review_dialog_edit_title : R.string.review_dialog_title)
                .setView(dialogView)
                .setNegativeButton(R.string.cta_cancel_profile, null)
                .setPositiveButton(R.string.review_submit, null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button submit = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (submit != null) {
                submit.setOnClickListener(v -> {
                    int rating = ratingBar == null ? 0 : Math.round(ratingBar.getRating());
                    if (rating < 1 || rating > 5) {
                        Toast.makeText(this, R.string.review_rating_required, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    getOrderReviewViewModel().submitReview(order.getId(), rating,
                            commentInput == null ? "" : commentInput.getText().toString());
                    dialog.dismiss();
                });
            }
        });
        dialog.show();
    }

    private void showReviewSubmissionResult(OrderReviewViewModel.ReviewSubmissionState state) {
        if (state == null || state.isLoading()) {
            return;
        }
        Toast.makeText(this,
                state.isSuccess() ? getString(R.string.review_saved_message) : getReviewErrorMessage(state),
                Toast.LENGTH_SHORT).show();
        getOrderReviewViewModel().consumeSubmissionState();
    }

    private String getReviewErrorMessage(OrderReviewViewModel.ReviewSubmissionState state) {
        String errorMessage = state.getErrorMessage();
        return errorMessage == null || errorMessage.trim().isEmpty()
                ? getString(R.string.review_save_failed_message)
                : errorMessage;
    }

    private void showReorderResult(OrderViewModel.ReorderState state) {
        if (state == null) {
            return;
        }
        if (state.isSuccess()) {
            Toast.makeText(this,
                    getString(R.string.reorder_success_format, state.getAddedItems()),
                    Toast.LENGTH_SHORT).show();
        } else if (state.getErrorMessage() != null) {
            Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
        getOrderViewModel().consumeReorderResult();
    }

    private void showOrderTab(boolean showHistory, OrderAdapter adapter, TextView emptyOrders,
                              TextView tabOngoing, TextView tabHistory,
                              View tabOngoingIndicator, View tabHistoryIndicator) {
        showingHistoryOrders = showHistory;
        updateOrderTabs(showHistory, tabOngoing, tabHistory, tabOngoingIndicator, tabHistoryIndicator);
        getOrderViewModel().getOngoingOrders().removeObservers(this);
        getOrderViewModel().getCompletedOrders().removeObservers(this);
        if (showHistory) {
            getOrderViewModel().getCompletedOrders().observe(this,
                    orders -> updateOrderList(orders, adapter, emptyOrders, true));
        } else {
            getOrderViewModel().getOngoingOrders().observe(this,
                    orders -> updateOrderList(orders, adapter, emptyOrders, false));
        }
    }

    private void updateOrderTabs(boolean showHistory, TextView tabOngoing, TextView tabHistory,
                                 View tabOngoingIndicator, View tabHistoryIndicator) {
        if (tabOngoing != null) {
            tabOngoing.setTextColor(getColor(showHistory
                    ? R.color.text_secondary_on_light
                    : R.color.text_on_light));
        }
        if (tabHistory != null) {
            tabHistory.setTextColor(getColor(showHistory
                    ? R.color.text_on_light
                    : R.color.text_secondary_on_light));
        }
        if (tabOngoingIndicator != null) {
            tabOngoingIndicator.setVisibility(showHistory ? View.GONE : View.VISIBLE);
        }
        if (tabHistoryIndicator != null) {
            tabHistoryIndicator.setVisibility(showHistory ? View.VISIBLE : View.GONE);
        }
    }

    private void updateOrderList(List<OrderListItem> orders, OrderAdapter adapter,
                                 TextView emptyOrders, boolean showHistory) {
        adapter.submitOrders(orders);
        boolean isEmpty = orders == null || orders.isEmpty();
        if (emptyOrders != null) {
            emptyOrders.setText(showHistory ? R.string.empty_history_orders : R.string.empty_ongoing_orders);
            emptyOrders.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    private void showRewards() {
        setContentView(R.layout.activity_rewards);

        RewardTransactionAdapter rewardAdapter = new RewardTransactionAdapter();
        RecyclerView rewardsHistory = findViewById(R.id.rvRewardsHistory);
        if (rewardsHistory != null) {
            rewardsHistory.setLayoutManager(new LinearLayoutManager(this));
            rewardsHistory.setAdapter(rewardAdapter);
        }

        View redeemDrinks = findViewById(R.id.btnRedeemDrinks);
        if (redeemDrinks != null) {
            redeemDrinks.setOnClickListener(v -> navigateTo(SCREEN_REDEEM));
        }
        View claimCard = findViewById(R.id.btnClaimLoyalty);
        if (claimCard != null) {
            claimCard.setOnClickListener(v -> getRewardViewModel().claimFullStampCard());
        }

        getRewardViewModel().getRewardState().removeObservers(this);
        getRewardViewModel().getRewardTransactions().removeObservers(this);
        getRewardViewModel().getRewardState().observe(this, this::updateRewardState);
        getRewardViewModel().getRewardTransactions().observe(this,
                transactions -> updateRewardHistory(transactions, rewardAdapter));
        setupPrimaryBottomNavigation(R.id.navRewards);
    }

    private void updateRewardState(RewardState rewardState) {
        int stampCount = rewardState == null ? 0 : RewardCalculator.capStampCount(rewardState.getStampCount());
        int totalPoints = rewardState == null ? 0 : rewardState.getTotalPoints();

        TextView pointsValue = findViewById(R.id.tvPointsValue);
        TextView claimCard = findViewById(R.id.btnClaimLoyalty);

        updateLoyaltyStampCount(R.id.tvRewardStampCount, stampCount);
        if (pointsValue != null) {
            pointsValue.setText(String.valueOf(totalPoints));
        }
        updateStampImages(stampCount);
        updateClaimCardButton(claimCard, RewardCalculator.canClaimStampCard(stampCount));
    }

    private void updateLoyaltyStampCount(int textViewId, int stampCount) {
        TextView stampCountText = findViewById(textViewId);
        if (stampCountText != null) {
            stampCountText.setText(String.valueOf(stampCount));
        }
    }

    private void updateStampImages(int stampCount) {
        int[] stampIds = {
                R.id.imgStamp1,
                R.id.imgStamp2,
                R.id.imgStamp3,
                R.id.imgStamp4,
                R.id.imgStamp5,
                R.id.imgStamp6,
                R.id.imgStamp7,
                R.id.imgStamp8
        };
        for (int index = 0; index < stampIds.length; index++) {
            ImageView stamp = findViewById(stampIds[index]);
            if (stamp != null) {
                stamp.setImageResource(index < stampCount
                        ? R.drawable.coffee_cup_blue
                        : R.drawable.coffee_cup_white);
            }
        }
    }

    private void updateClaimCardButton(TextView claimCard, boolean canClaim) {
        if (claimCard == null) {
            return;
        }
        claimCard.setEnabled(canClaim);
        claimCard.setAlpha(canClaim ? 1.00f : 0.55f);
        claimCard.setText(canClaim ? R.string.cta_claim_loyalty : R.string.cta_complete_loyalty);
    }

    private void updateRewardHistory(List<RewardTransaction> transactions,
                                     RewardTransactionAdapter adapter) {
        adapter.submitTransactions(transactions);
        boolean isEmpty = transactions == null || transactions.isEmpty();
        View emptyHistory = findViewById(R.id.tvEmptyRewardHistory);
        if (emptyHistory != null) {
            emptyHistory.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    private void showRedeem() {
        setContentView(R.layout.activity_redeem);
        applyTopSystemBarInset(R.id.redeemRoot);

        RewardProductAdapter rewardProductAdapter = new RewardProductAdapter(
                RewardCatalog.getRewards(),
                rewardProduct -> getRewardViewModel().redeemReward(rewardProduct));
        RecyclerView redeemList = findViewById(R.id.rvRedeemList);
        if (redeemList != null) {
            redeemList.setLayoutManager(new LinearLayoutManager(this));
            redeemList.setAdapter(rewardProductAdapter);
        }

        getRewardViewModel().getRewardState().removeObservers(this);
        getRewardViewModel().getRedemptionInProgress().removeObservers(this);
        getRewardViewModel().getRedemptionResult().removeObservers(this);
        getRewardViewModel().getRewardState().observe(this, this::updateRedeemPoints);
        getRewardViewModel().getRedemptionInProgress().observe(this,
                inProgress -> rewardProductAdapter.setRedeemEnabled(!Boolean.TRUE.equals(inProgress)));
        getRewardViewModel().getRedemptionResult().observe(this, this::showRedemptionResult);

        setClickListener(R.id.btnBack, this::goBackOrHome);
    }

    private void updateRedeemPoints(RewardState rewardState) {
        TextView pointsValue = findViewById(R.id.tvRedeemPointsValue);
        if (pointsValue != null) {
            int totalPoints = rewardState == null ? 0 : rewardState.getTotalPoints();
            pointsValue.setText(getString(R.string.redeem_points_available_format, totalPoints));
        }
    }

    private void showRedemptionResult(RewardRepository.RedemptionResult result) {
        if (result == null) {
            return;
        }
        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        getRewardViewModel().clearRedemptionResult();
    }

    private void showProfile() {
        setContentView(R.layout.activity_profile);
        applyTopSystemBarInset(R.id.profileRoot);

        getProfileViewModel().getProfile().removeObservers(this);
        getProfileViewModel().getValidationResult().removeObservers(this);
        getProfileViewModel().getProfile().observe(this, this::updateProfileViews);
        getProfileViewModel().getValidationResult().observe(this, this::showProfileValidationResult);

        setClickListener(R.id.btnEditName, this::enterProfileEditMode);
        setClickListener(R.id.btnEditPhone, this::enterProfileEditMode);
        setClickListener(R.id.btnEditEmail, this::enterProfileEditMode);
        setClickListener(R.id.btnEditAddress, this::enterProfileEditMode);
        setClickListener(R.id.btnSaveProfile, this::saveProfileEdits);
        setClickListener(R.id.btnCancelProfile, this::cancelProfileEdits);
        setupDarkModeSwitch();
        setProfileEditing(profileEditMode);
        getProfileViewModel().reloadProfile();
        setClickListener(R.id.btnBack, this::goBackOrHome);
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

    private void setupDarkModeSwitch() {
        Switch darkModeSwitch = findViewById(R.id.switchDarkMode);
        if (darkModeSwitch == null) {
            return;
        }
        darkModeSwitch.setOnCheckedChangeListener(null);
        darkModeSwitch.setChecked(getThemeRepository().isDarkModeEnabled());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getThemeRepository().setDarkModeEnabled(isChecked);
            applyThemeMode(isChecked);
        });
    }

    private void updateProfileViews(UserProfile profile) {
        if (profile == null) {
            return;
        }
        setText(R.id.tvFullName, profile.getFullName());
        setText(R.id.tvPhoneNumber, profile.getPhone());
        setText(R.id.tvEmail, profile.getEmail());
        setText(R.id.tvAddress, profile.getAddress());
        if (!profileEditMode) {
            setEditText(R.id.etFullName, profile.getFullName());
            setEditText(R.id.etPhoneNumber, profile.getPhone());
            setEditText(R.id.etEmail, profile.getEmail());
            setEditText(R.id.etAddress, profile.getAddress());
        }
    }

    private void enterProfileEditMode() {
        profileEditMode = true;
        clearProfileErrors();
        UserProfile profile = getProfileViewModel().getProfile().getValue();
        if (profile != null) {
            setEditText(R.id.etFullName, profile.getFullName());
            setEditText(R.id.etPhoneNumber, profile.getPhone());
            setEditText(R.id.etEmail, profile.getEmail());
            setEditText(R.id.etAddress, profile.getAddress());
        }
        setProfileEditing(true);
    }

    private void saveProfileEdits() {
        clearProfileErrors();
        boolean saved = getProfileViewModel().saveProfile(
                getEditText(R.id.etFullName),
                getEditText(R.id.etPhoneNumber),
                getEditText(R.id.etEmail),
                getEditText(R.id.etAddress));
        if (saved) {
            profileEditMode = false;
            setProfileEditing(false);
            Toast.makeText(this, R.string.profile_saved_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelProfileEdits() {
        profileEditMode = false;
        clearProfileErrors();
        getProfileViewModel().reloadProfile();
        setProfileEditing(false);
    }

    private void showProfileValidationResult(ProfileViewModel.ValidationResult result) {
        if (result == null || result.isValid()) {
            return;
        }
        int fieldId = getProfileFieldId(result.getField());
        if (fieldId != 0) {
            EditText field = findViewById(fieldId);
            if (field != null) {
                field.setError(result.getMessage());
                field.requestFocus();
            }
        }
        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        getProfileViewModel().clearValidationResult();
    }

    private int getProfileFieldId(ProfileViewModel.Field field) {
        if (field == ProfileViewModel.Field.FULL_NAME) {
            return R.id.etFullName;
        }
        if (field == ProfileViewModel.Field.PHONE) {
            return R.id.etPhoneNumber;
        }
        if (field == ProfileViewModel.Field.EMAIL) {
            return R.id.etEmail;
        }
        if (field == ProfileViewModel.Field.ADDRESS) {
            return R.id.etAddress;
        }
        return 0;
    }

    private void setProfileEditing(boolean editing) {
        int textVisibility = editing ? View.GONE : View.VISIBLE;
        int editVisibility = editing ? View.VISIBLE : View.GONE;
        setVisibility(R.id.tvFullName, textVisibility);
        setVisibility(R.id.tvPhoneNumber, textVisibility);
        setVisibility(R.id.tvEmail, textVisibility);
        setVisibility(R.id.tvAddress, textVisibility);
        setVisibility(R.id.etFullName, editVisibility);
        setVisibility(R.id.etPhoneNumber, editVisibility);
        setVisibility(R.id.etEmail, editVisibility);
        setVisibility(R.id.etAddress, editVisibility);
        setVisibility(R.id.layoutProfileActions, editVisibility);
        setVisibility(R.id.btnEditName, textVisibility);
        setVisibility(R.id.btnEditPhone, textVisibility);
        setVisibility(R.id.btnEditEmail, textVisibility);
        setVisibility(R.id.btnEditAddress, textVisibility);
    }

    private void clearProfileErrors() {
        clearEditTextError(R.id.etFullName);
        clearEditTextError(R.id.etPhoneNumber);
        clearEditTextError(R.id.etEmail);
        clearEditTextError(R.id.etAddress);
    }

    private void setupPrimaryBottomNavigation(int selectedItemId) {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setSelectedItemId(selectedItemId);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navHome) {
                navigateToPrimary(SCREEN_HOME);
                return true;
            }
            if (itemId == R.id.navOrders) {
                navigateToPrimary(SCREEN_ORDERS);
                return true;
            }
            if (itemId == R.id.navRewards) {
                navigateToPrimary(SCREEN_REWARDS);
                return true;
            }
            return false;
        });
    }

    private void goBackOrHome() {
        if (currentScreen == SCREEN_PROFILE) {
            profileEditMode = false;
        }
        if (!backStack.isEmpty()) {
            int previousScreen = backStack.remove(backStack.size() - 1);
            showScreen(previousScreen);
        } else {
            showScreen(SCREEN_HOME);
        }
    }

    private boolean isPrimaryScreen(int screen) {
        return screen == SCREEN_HOME || screen == SCREEN_ORDERS || screen == SCREEN_REWARDS;
    }

    private void setClickListener(int viewId, ClickAction action) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> action.run());
        }
    }

    private CartViewModel getCartViewModel() {
        if (cartViewModel == null) {
            cartViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                    .get(CartViewModel.class);
        }
        return cartViewModel;
    }

    private CheckoutViewModel getCheckoutViewModel() {
        if (checkoutViewModel == null) {
            checkoutViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                    .get(CheckoutViewModel.class);
        }
        return checkoutViewModel;
    }

    private OrderViewModel getOrderViewModel() {
        if (orderViewModel == null) {
            orderViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                    .get(OrderViewModel.class);
        }
        return orderViewModel;
    }

    private OrderReviewViewModel getOrderReviewViewModel() {
        if (orderReviewViewModel == null) {
            orderReviewViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                    .get(OrderReviewViewModel.class);
        }
        return orderReviewViewModel;
    }

    private RewardViewModel getRewardViewModel() {
        if (rewardViewModel == null) {
            rewardViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                    .get(RewardViewModel.class);
        }
        return rewardViewModel;
    }

    private ProfileViewModel getProfileViewModel() {
        if (profileViewModel == null) {
            profileViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                    .get(ProfileViewModel.class);
        }
        return profileViewModel;
    }

    private FavoriteRepository getFavoriteRepository() {
        if (favoriteRepository == null) {
            favoriteRepository = new FavoriteRepository(getApplication());
        }
        return favoriteRepository;
    }

    private ThemeRepository getThemeRepository() {
        if (themeRepository == null) {
            themeRepository = new ThemeRepository(getApplication());
        }
        return themeRepository;
    }

    private void applySavedThemeMode() {
        applyThemeMode(getThemeRepository().isDarkModeEnabled());
    }

    private void applyThemeMode(boolean darkModeEnabled) {
        AppCompatDelegate.setDefaultNightMode(darkModeEnabled
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void updateCartBadge(List<CartItem> items) {
        int badgeCount = calculateCartBadgeCount(items);
        TextView badge = findViewById(R.id.tvCartBadge);
        if (badge != null) {
            badge.setText(String.valueOf(badgeCount));
            badge.setVisibility(badgeCount > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private int calculateCartBadgeCount(List<CartItem> items) {
        int badgeCount = 0;
        if (items != null) {
            for (CartItem item : items) {
                badgeCount += item.getQuantity();
            }
        }
        return badgeCount;
    }

    private void setText(int viewId, String value) {
        TextView view = findViewById(viewId);
        if (view != null) {
            view.setText(value);
        }
    }

    private void setEditText(int viewId, String value) {
        EditText view = findViewById(viewId);
        if (view != null) {
            view.setText(value);
        }
    }

    private String getEditText(int viewId) {
        EditText view = findViewById(viewId);
        return view == null ? "" : view.getText().toString();
    }

    private void clearEditTextError(int viewId) {
        EditText view = findViewById(viewId);
        if (view != null) {
            view.setError(null);
        }
    }

    private void setVisibility(int viewId, int visibility) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void updateCartSummary(List<CartItem> items) {
        boolean isEmpty = items == null || items.isEmpty();
        double total = 0.00;
        if (items != null) {
            for (CartItem item : items) {
                total += item.getTotalPrice();
            }
        }

        TextView totalPrice = findViewById(R.id.tvTotalPrice);
        TextView emptyCart = findViewById(R.id.tvEmptyCart);
        View checkout = findViewById(R.id.btnCheckout);

        if (totalPrice != null) {
            totalPrice.setText(String.format(Locale.US, "$%.2f", total));
        }
        if (emptyCart != null) {
            emptyCart.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (checkout != null) {
            boolean checkoutEnabled = !isEmpty && !checkoutInProgress;
            checkout.setEnabled(checkoutEnabled);
            checkout.setAlpha(checkoutEnabled ? 1.00f : 0.45f);
        }
    }

    private void updateCheckoutSummary(CheckoutSummary summary) {
        if (summary == null) {
            return;
        }
        setText(R.id.tvSubtotal, getString(R.string.checkout_subtotal_format, summary.getSubtotal()));
        setText(R.id.tvLoyaltyDiscount,
                getString(R.string.checkout_loyalty_discount_format, summary.getLoyaltyDiscount()));
        setText(R.id.tvPromoDiscount,
                getString(R.string.checkout_promo_discount_format, summary.getPromoDiscount()));
        setText(R.id.tvFinalTotal,
                getString(R.string.checkout_final_total_format, summary.getFinalTotal()));

        TextView promoMessage = findViewById(R.id.tvPromoMessage);
        if (promoMessage != null) {
            String message = summary.getPromoMessage();
            promoMessage.setText(message == null ? "" : message);
            promoMessage.setVisibility(message == null || message.isEmpty() ? View.GONE : View.VISIBLE);
            promoMessage.setTextColor(getColor(summary.isPromoAccepted()
                    ? R.color.primary
                    : R.color.delete_red));
        }
        updatePlaceOrderButton();
    }

    private void updatePlaceOrderButton() {
        View placeOrder = findViewById(R.id.btnPlaceOrder);
        if (placeOrder == null) {
            return;
        }
        boolean cartHasItems = currentCartItems != null && !currentCartItems.isEmpty();
        boolean hasAddress = !getEditText(R.id.etDeliveryAddress).trim().isEmpty();
        boolean deliveryScheduleValid = getCheckoutViewModel().isDeliveryScheduleValid();
        boolean enabled = cartHasItems && hasAddress && deliveryScheduleValid && !checkoutInProgress;
        placeOrder.setEnabled(enabled);
        placeOrder.setAlpha(enabled ? 1.00f : 0.45f);
    }

    private void attachSwipeToDelete(RecyclerView recyclerView, CartAdapter cartAdapter) {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                CartItem item = cartAdapter.getItemAt(position);
                if (item != null) {
                    getCartViewModel().deleteCartItem(item);
                } else if (position != RecyclerView.NO_POSITION) {
                    cartAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
                    drawSwipeDeleteIndicator(c, viewHolder.itemView, dX);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    private void drawSwipeDeleteIndicator(Canvas canvas, View itemView, float dX) {
        Drawable background = getDrawable(R.drawable.bg_delete_action);
        Drawable deleteIcon = getDrawable(R.drawable.delete);
        if (background == null || deleteIcon == null) {
            return;
        }

        int right = itemView.getRight();
        int left = Math.max(right + (int) dX, right - itemView.getHeight());
        background.setBounds(left, itemView.getTop(), right, itemView.getBottom());
        background.draw(canvas);

        int iconWidth = deleteIcon.getIntrinsicWidth() > 0 ? deleteIcon.getIntrinsicWidth() : 24;
        int iconHeight = deleteIcon.getIntrinsicHeight() > 0 ? deleteIcon.getIntrinsicHeight() : 24;
        int iconMargin = (itemView.getHeight() - iconHeight) / 2;
        int iconLeft = right - iconMargin - iconWidth;
        int iconTop = itemView.getTop() + (itemView.getHeight() - iconHeight) / 2;
        deleteIcon.mutate().setTint(getColor(R.color.white));
        deleteIcon.setBounds(iconLeft, iconTop, iconLeft + iconWidth, iconTop + iconHeight);
        deleteIcon.draw(canvas);
    }

    private interface ClickAction {
        void run();
    }
}
