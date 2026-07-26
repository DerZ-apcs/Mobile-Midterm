package com.example.midterm_application;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
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

    private final ArrayList<Integer> backStack = new ArrayList<>();
    private int currentScreen = SCREEN_HOME;
    private int selectedCoffeeId = 1;
    private String selectedCoffeeName = "Americano";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentScreen = savedInstanceState.getInt(STATE_CURRENT_SCREEN, SCREEN_HOME);
            selectedCoffeeId = savedInstanceState.getInt(STATE_SELECTED_COFFEE_ID, 1);
            selectedCoffeeName = savedInstanceState.getString(STATE_SELECTED_COFFEE_NAME, "Americano");
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
    }

    @Override
    public void onBackPressed() {
        if (currentScreen == SCREEN_DETAILS) {
            backStack.clear();
            showScreen(SCREEN_HOME);
            return;
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

        super.onBackPressed();
    }

    private void navigateTo(int screen) {
        if (currentScreen != screen) {
            backStack.add(currentScreen);
        }
        showScreen(screen);
    }

    private void navigateToDetails(int coffeeId, String coffeeName) {
        selectedCoffeeId = coffeeId;
        selectedCoffeeName = coffeeName;
        navigateTo(SCREEN_DETAILS);
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
            coffeeGrid.setAdapter(new CoffeeAdapter(createNavigationCoffeeItems(), this::navigateToDetails));
        }

        setClickListener(R.id.btnCart, () -> navigateTo(SCREEN_CART));
        setClickListener(R.id.btnProfile, () -> navigateTo(SCREEN_PROFILE));
        setClickListener(R.id.navHome, () -> showScreen(SCREEN_HOME));
        setClickListener(R.id.navRewards, () -> navigateTo(SCREEN_REWARDS));
        setClickListener(R.id.navHistory, () -> navigateTo(SCREEN_ORDERS));
    }

    private void showDetails() {
        setContentView(R.layout.activity_coffee_details);

        TextView coffeeName = findViewById(R.id.tvCoffeeName);
        if (coffeeName != null) {
            coffeeName.setText(selectedCoffeeName);
        }

        setClickListener(R.id.btnBack, () -> {
            backStack.clear();
            showScreen(SCREEN_HOME);
        });
        setClickListener(R.id.btnCart, () -> navigateTo(SCREEN_CART));
        setClickListener(R.id.btnAddToCart, () -> navigateTo(SCREEN_CART));
    }

    private void showCart() {
        setContentView(R.layout.activity_cart);

        setClickListener(R.id.btnBack, this::goBackOrHome);
        setClickListener(R.id.btnCheckout, () -> navigateTo(SCREEN_ORDER_SUCCESS));
    }

    private void showOrderSuccess() {
        setContentView(R.layout.activity_order_success);

        setClickListener(R.id.btnTrackOrder, () -> navigateTo(SCREEN_ORDERS));
    }

    private void showOrders() {
        setContentView(R.layout.activity_my_order);

        setClickListener(R.id.navHome, () -> navigateTo(SCREEN_HOME));
        setClickListener(R.id.navRewards, () -> navigateTo(SCREEN_REWARDS));
        setClickListener(R.id.navHistory, () -> showScreen(SCREEN_ORDERS));
    }

    private void showRewards() {
        setContentView(R.layout.activity_rewards);

        setClickListener(R.id.btnRedeemDrinks, () -> navigateTo(SCREEN_REDEEM));
        setClickListener(R.id.navOrders, () -> navigateTo(SCREEN_ORDERS));
        setClickListener(R.id.navRewards, () -> showScreen(SCREEN_REWARDS));
        setClickListener(R.id.navAccount, () -> navigateTo(SCREEN_PROFILE));
    }

    private void showRedeem() {
        setContentView(R.layout.activity_redeem);

        setClickListener(R.id.btnBack, this::goBackOrHome);
    }

    private void showProfile() {
        setContentView(R.layout.activity_profile);
    }

    private void goBackOrHome() {
        if (!backStack.isEmpty()) {
            int previousScreen = backStack.remove(backStack.size() - 1);
            showScreen(previousScreen);
        } else {
            showScreen(SCREEN_HOME);
        }
    }

    private void setClickListener(int viewId, ClickAction action) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> action.run());
        }
    }

    private List<CoffeeItem> createNavigationCoffeeItems() {
        ArrayList<CoffeeItem> items = new ArrayList<>();
        items.add(new CoffeeItem(1, getString(R.string.coffee_americano)));
        items.add(new CoffeeItem(2, getString(R.string.coffee_cappuccino)));
        items.add(new CoffeeItem(3, getString(R.string.coffee_mocha)));
        items.add(new CoffeeItem(4, getString(R.string.coffee_flat_white)));
        return items;
    }

    private interface ClickAction {
        void run();
    }

    private interface CoffeeClickListener {
        void onCoffeeClicked(int coffeeId, String coffeeName);
    }

    private static class CoffeeItem {
        final int id;
        final String name;

        CoffeeItem(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {
        private final List<CoffeeItem> items;
        private final CoffeeClickListener clickListener;

        CoffeeAdapter(List<CoffeeItem> items, CoffeeClickListener clickListener) {
            this.items = items;
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_coffee_card, parent, false);
            return new CoffeeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
            CoffeeItem item = items.get(position);
            holder.name.setText(item.name);
            holder.itemView.setOnClickListener(v -> clickListener.onCoffeeClicked(item.id, item.name));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class CoffeeViewHolder extends RecyclerView.ViewHolder {
            final TextView name;

            CoffeeViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tvCoffeeName);
            }
        }
    }
}
