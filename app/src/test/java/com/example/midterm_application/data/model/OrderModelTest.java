package com.example.midterm_application.data.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OrderModelTest {
    private static final double DELTA = 0.001;

    @Test
    public void orderStartsWithOngoingStatus() {
        Order order = new Order(1000L, 12.50);

        assertEquals(Order.STATUS_ONGOING, order.getStatus());
        assertEquals(1000L, order.getCreatedAt());
        assertEquals(12.50, order.getTotalPrice(), DELTA);
        assertEquals(12.50, order.getSubtotal(), DELTA);
        assertEquals(12.50, order.getFinalTotal(), DELTA);
        assertEquals(Order.DELIVERY_ASAP, order.getDeliveryType());
        assertEquals(0L, order.getScheduledAt());
    }

    @Test
    public void enhancedCheckoutOrderPersistsSnapshotFields() {
        Order order = new Order(
                2000L,
                20.00,
                "CODECUP10",
                1.50,
                5.00,
                13.50,
                "123 Checkout Lane",
                true);

        assertEquals(20.00, order.getSubtotal(), DELTA);
        assertEquals("CODECUP10", order.getPromoCode());
        assertEquals(1.50, order.getPromoDiscount(), DELTA);
        assertEquals(5.00, order.getLoyaltyDiscount(), DELTA);
        assertEquals(13.50, order.getFinalTotal(), DELTA);
        assertEquals(13.50, order.getTotalPrice(), DELTA);
        assertEquals("123 Checkout Lane", order.getDeliveryAddress());
        assertEquals(true, order.isLoyaltyRewardUsed());
        assertEquals(Order.DELIVERY_ASAP, order.getDeliveryType());
        assertEquals(0L, order.getScheduledAt());
    }

    @Test
    public void scheduledOrderPersistsDeliverySnapshot() {
        Order order = new Order(
                2000L,
                20.00,
                "CODECUP10",
                1.50,
                5.00,
                13.50,
                "123 Checkout Lane",
                true,
                Order.DELIVERY_SCHEDULED,
                3000L);

        assertEquals(Order.DELIVERY_SCHEDULED, order.getDeliveryType());
        assertEquals(3000L, order.getScheduledAt());
    }

    @Test
    public void orderSupportsCompletedStatus() {
        Order order = new Order(1000L, 12.50);

        order.setStatus(Order.STATUS_COMPLETED);

        assertEquals(Order.STATUS_COMPLETED, order.getStatus());
    }

    @Test
    public void orderItemPreservesCartSnapshotFields() {
        OrderItem item = new OrderItem(
                42L,
                7,
                "Mocha",
                101,
                "DOUBLE",
                "LARGE",
                "LESS_ICE",
                3,
                5.50,
                16.50,
                "No sugar");

        assertEquals(42L, item.getOrderId());
        assertEquals(7, item.getCoffeeId());
        assertEquals("Mocha", item.getCoffeeName());
        assertEquals(101, item.getImageResId());
        assertEquals("DOUBLE", item.getShot());
        assertEquals("LARGE", item.getSize());
        assertEquals("LESS_ICE", item.getIce());
        assertEquals(3, item.getQuantity());
        assertEquals(5.50, item.getUnitPrice(), DELTA);
        assertEquals(16.50, item.getTotalPrice(), DELTA);
        assertEquals("No sugar", item.getNote());
    }

    @Test
    public void cartItemPreservesOptionalNote() {
        CartItem item = new CartItem(
                7,
                "Mocha",
                101,
                "DOUBLE",
                "LARGE",
                "LESS_ICE",
                3,
                5.50,
                16.50,
                "Extra hot");

        assertEquals("Extra hot", item.getNote());
    }

    @Test
    public void cartItemAllowsEmptyNote() {
        CartItem item = new CartItem(
                7,
                "Mocha",
                101,
                "DOUBLE",
                "LARGE",
                "LESS_ICE",
                3,
                5.50,
                16.50,
                "");

        assertEquals("", item.getNote());
    }
}
