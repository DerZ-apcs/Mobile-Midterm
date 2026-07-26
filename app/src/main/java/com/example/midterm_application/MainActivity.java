package com.example.midterm_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.OrderListItem;
import com.example.midterm_application.data.repository.CoffeeRepository;
import com.example.midterm_application.ui.CartAdapter;
import com.example.midterm_application.ui.CoffeeAdapter;
import com.example.midterm_application.ui.OrderAdapter;
import com.example.midterm_application.viewmodel.CartViewModel;
import com.example.midterm_application.viewmodel.OrderViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends ComponentActivity {
    public static final String EXTRA_OPEN_CART = "com.example.midterm_application.EXTRA_OPEN_CART";

    private static final int REQUEST_COFFEE_DETAILS = 1001;

    private static final int SCREEN_HOME = 1;
    private static final int SCREEN_DETAILS = 2;
    private static final int SCREEN_CART = 3;
    private static final int SCREEN_ORDER_SUCCESS = 4;
    private static final int SCREEN_ORDERS = 5;
    private static final int SCREEN_REWARDS = 6;
    private static final int SCREEN_REDEEM = 7;
    private static final int SCREEN_PROFILE = 8;

    private static final String STATE_CURRENT_SCREEN = "current_screen";
    private static final String STATE_SELECTED_COFFEE_ID = "selected_coffee_id";
    private static final String STATE_SELECTED_COFFEE_NAME = "selected_coffee_name";
    private static final String STATE_BACK_STACK = "back_stack";
    private static final String STATE_SHOWING_HISTORY_ORDERS = "showing_history_orders";

    private final ArrayList<Integer> backStack = new ArrayList<>();
    private int currentScreen = SCREEN_HOME;
    private int selectedCoffeeId = 1;
    private String selectedCoffeeName = "Americano";
    private CartViewModel cartViewModel;
    private OrderViewModel orderViewModel;
    private List<CartItem> currentCartItems = new ArrayList<>();
    private boolean checkoutInProgress;
    private boolean showingHistoryOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentScreen = savedInstanceState.getInt(STATE_CURRENT_SCREEN, SCREEN_HOME);
            selectedCoffeeId = savedInstanceState.getInt(STATE_SELECTED_COFFEE_ID, 1);
            selectedCoffeeName = savedInstanceState.getString(STATE_SELECTED_COFFEE_NAME, "Americano");
            showingHistoryOrders = savedInstanceState.getBoolean(STATE_SHOWING_HISTORY_ORDERS, false);
            ArrayList<Integer> restoredStack = savedInstanceState.getIntegerArrayList(STATE_BACK_STACK);
            if (restoredStack != null) {
                backStack.addAll(restoredStack);
            }
        }

        showScreen(currentScreen);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_SCREEN, currentScreen);
        outState.putInt(STATE_SELECTED_COFFEE_ID, selectedCoffeeId);
        outState.putString(STATE_SELECTED_COFFEE_NAME, selectedCoffeeName);
        outState.putIntegerArrayList(STATE_BACK_STACK, backStack);
        outState.putBoolean(STATE_SHOWING_HISTORY_ORDERS, showingHistoryOrders);
    }

    @Override
    public void onBackPressed() {
        if (!backStack.isEmpty()) {
            int previousScreen = backStack.remove(backStack.size() - 1);
            showScreen(previousScreen);
            return;
        }

        if (currentScreen != SCREEN_HOME) {
            showScreen(SCREEN_HOME);
            return;
        }

        super.onBackPressed();
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

        RecyclerView coffeeGrid = findViewById(R.id.rvCoffeeGrid);
        if (coffeeGrid != null) {
            coffeeGrid.setAdapter(new CoffeeAdapter(CoffeeRepository.getAllCoffees(),
                    coffee -> openCoffeeDetails(coffee.getId())));
        }

        setClickListener(R.id.btnCart, () -> navigateTo(SCREEN_CART));
        setClickListener(R.id.btnProfile, () -> navigateTo(SCREEN_PROFILE));
        setupPrimaryBottomNavigation(R.id.navHome);
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
            checkout.setOnClickListener(v -> getOrderViewModel().checkout());
        }

        CartAdapter cartAdapter = new CartAdapter();
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

        getOrderViewModel().getCheckoutState().removeObservers(this);
        getOrderViewModel().getCheckoutState().observe(this, state -> {
            if (state == null) {
                return;
            }

            checkoutInProgress = state.isLoading();
            updateCartSummary(currentCartItems);
            if (state.isSuccess()) {
                getOrderViewModel().consumeCheckoutResult();
                navigateTo(SCREEN_ORDER_SUCCESS);
                return;
            }
            if (state.getErrorMessage() != null) {
                Toast.makeText(this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();
                getOrderViewModel().consumeCheckoutResult();
            }
        });
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

        OrderAdapter orderAdapter = new OrderAdapter(order -> getOrderViewModel().completeOrder(order.getId()));
        RecyclerView orderList = findViewById(R.id.rvOrderList);
        if (orderList != null) {
            orderList.setLayoutManager(new LinearLayoutManager(this));
            orderList.setAdapter(orderAdapter);
        }

        TextView tabOngoing = findViewById(R.id.tabOngoing);
        TextView tabHistory = findViewById(R.id.tabHistory);
        TextView emptyOrders = findViewById(R.id.tvEmptyOrders);
        if (tabOngoing != null) {
            tabOngoing.setOnClickListener(v -> showOrderTab(false, orderAdapter, emptyOrders, tabOngoing, tabHistory));
        }
        if (tabHistory != null) {
            tabHistory.setOnClickListener(v -> showOrderTab(true, orderAdapter, emptyOrders, tabOngoing, tabHistory));
        }
        showOrderTab(showingHistoryOrders, orderAdapter, emptyOrders, tabOngoing, tabHistory);

        setupPrimaryBottomNavigation(R.id.navOrders);
    }

    private void showOrderTab(boolean showHistory, OrderAdapter adapter, TextView emptyOrders,
                              TextView tabOngoing, TextView tabHistory) {
        showingHistoryOrders = showHistory;
        updateOrderTabs(showHistory, tabOngoing, tabHistory);
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

    private void updateOrderTabs(boolean showHistory, TextView tabOngoing, TextView tabHistory) {
        if (tabOngoing != null) {
            tabOngoing.setBackgroundResource(showHistory ? 0 : R.drawable.bg_tab_active_border);
            tabOngoing.setTextColor(getColor(showHistory ? R.color.gray_400 : R.color.order_text));
        }
        if (tabHistory != null) {
            tabHistory.setBackgroundResource(showHistory ? R.drawable.bg_tab_active_border : 0);
            tabHistory.setTextColor(getColor(showHistory ? R.color.order_text : R.color.gray_400));
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

        setClickListener(R.id.btnRedeemDrinks, () -> navigateTo(SCREEN_REDEEM));
        setupPrimaryBottomNavigation(R.id.navRewards);
    }

    private void showRedeem() {
        setContentView(R.layout.activity_redeem);

        setClickListener(R.id.btnBack, this::goBackOrHome);
    }

    private void showProfile() {
        setContentView(R.layout.activity_profile);

        setClickListener(R.id.btnBack, this::goBackOrHome);
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

    private OrderViewModel getOrderViewModel() {
        if (orderViewModel == null) {
            orderViewModel = new ViewModelProvider(this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                    .get(OrderViewModel.class);
        }
        return orderViewModel;
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
                CartItem item = cartAdapter.getItemAt(viewHolder.getBindingAdapterPosition());
                if (item != null) {
                    getCartViewModel().deleteCartItem(item);
                }
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    private interface ClickAction {
        void run();
    }
}
