package com.example.midterm_application.data.repository;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.Coffee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class CoffeeRepository {
    private static final List<Coffee> COFFEES = Collections.unmodifiableList(Arrays.asList(
            new Coffee(1, "Americano", R.drawable.ic_coffee_cup, 3.00),
            new Coffee(2, "Cappuccino", R.drawable.ic_cup_outline, 4.00),
            new Coffee(3, "Mocha", R.drawable.ic_coffee_cup, 4.50),
            new Coffee(4, "Flat White", R.drawable.ic_cup_outline, 4.00)
    ));

    private CoffeeRepository() {
    }

    public static List<Coffee> getAllCoffees() {
        return new ArrayList<>(COFFEES);
    }

    public static List<Coffee> searchByName(String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.US);
        if (normalizedQuery.isEmpty()) {
            return getAllCoffees();
        }

        List<Coffee> results = new ArrayList<>();
        for (Coffee coffee : COFFEES) {
            if (coffee.getName().toLowerCase(Locale.US).contains(normalizedQuery)) {
                results.add(coffee);
            }
        }
        return results;
    }

    public static Coffee getCoffeeById(int id) {
        for (Coffee coffee : COFFEES) {
            if (coffee.getId() == id) {
                return coffee;
            }
        }
        return null;
    }
}
