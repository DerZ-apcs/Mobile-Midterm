package com.example.midterm_application.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderDetails {
    private final Order order;
    private final List<OrderItem> items;
    private final OrderReview review;

    public OrderDetails(Order order, List<OrderItem> items, OrderReview review) {
        this.order = order;
        this.items = items == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(items));
        this.review = review;
    }

    public Order getOrder() {
        return order;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderReview getReview() {
        return review;
    }
}
