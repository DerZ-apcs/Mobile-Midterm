package com.example.midterm_application.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.CheckoutSummary;
import com.example.midterm_application.data.model.PromoCode;
import com.example.midterm_application.data.repository.PromoRepository;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class CheckoutPriceCalculatorTest {
    private static final double DELTA = 0.001;

    private final PromoRepository promoRepository = new PromoRepository();

    @Test
    public void scenarioA_noDiscountsKeepsSubtotalAsFinalTotal() {
        CheckoutSummary summary = calculate(Collections.singletonList(item("Americano", 10.00, 2)),
                "", false, false);

        assertEquals(20.00, summary.getSubtotal(), DELTA);
        assertEquals(0.00, summary.getLoyaltyDiscount(), DELTA);
        assertEquals(0.00, summary.getPromoDiscount(), DELTA);
        assertEquals(20.00, summary.getFinalTotal(), DELTA);
    }

    @Test
    public void scenarioB_checkoutLoyaltyNoLongerAppliesFreeDrinkDiscount() {
        CheckoutSummary summary = calculate(Collections.singletonList(item("Latte", 5.00, 4)),
                "", true, true);

        assertEquals(20.00, summary.getSubtotal(), DELTA);
        assertEquals(0.00, summary.getLoyaltyDiscount(), DELTA);
        assertEquals(0.00, summary.getPromoDiscount(), DELTA);
        assertEquals(20.00, summary.getFinalTotal(), DELTA);
    }

    @Test
    public void scenarioC_codeCup10AppliesToPaidSubtotalWithoutCheckoutLoyalty() {
        CheckoutSummary summary = calculate(Collections.singletonList(item("Latte", 5.00, 4)),
                "CODECUP10", true, true);

        assertEquals(20.00, summary.getSubtotal(), DELTA);
        assertEquals(0.00, summary.getLoyaltyDiscount(), DELTA);
        assertEquals(2.00, summary.getPromoDiscount(), DELTA);
        assertEquals(18.00, summary.getFinalTotal(), DELTA);
    }

    @Test
    public void scenarioD_quantityTwoReceivesNoCheckoutFreeUnit() {
        CheckoutSummary summary = calculate(Collections.singletonList(item("Americano", 4.50, 2)),
                "", true, true);

        assertEquals(9.00, summary.getSubtotal(), DELTA);
        assertEquals(0.00, summary.getLoyaltyDiscount(), DELTA);
        assertEquals(9.00, summary.getFinalTotal(), DELTA);
    }

    @Test
    public void scenarioE_stampSevenCannotApplyLoyaltyDiscount() {
        CheckoutSummary summary = calculate(Collections.singletonList(item("Latte", 5.00, 4)),
                "", true, false);

        assertEquals(20.00, summary.getSubtotal(), DELTA);
        assertEquals(0.00, summary.getLoyaltyDiscount(), DELTA);
        assertEquals(20.00, summary.getFinalTotal(), DELTA);
    }

    @Test
    public void rewardCartItemsContributeZeroToTotals() {
        CheckoutSummary summary = calculate(Arrays.asList(
                        item("Americano", 4.50, 2),
                        rewardItem("Mocha"),
                        item("Flat White", 5.00, 3)),
                "", true, true);

        assertEquals(24.00, summary.getSubtotal(), DELTA);
        assertEquals(0.00, summary.getLoyaltyDiscount(), DELTA);
        assertEquals(24.00, summary.getFinalTotal(), DELTA);
    }

    @Test
    public void promoCodeMatchingIsCaseInsensitiveAndTrimsWhitespace() {
        CheckoutSummary summary = calculate(Collections.singletonList(item("Latte", 10.00, 2)),
                "  codecup10  ", false, false);

        assertTrue(summary.isPromoAccepted());
        assertEquals("CODECUP10", summary.getPromoCode());
        assertEquals(2.00, summary.getPromoDiscount(), DELTA);
        assertEquals(18.00, summary.getFinalTotal(), DELTA);
    }

    @Test
    public void welcome5RequiresMinimumEligibleSubtotal() {
        CheckoutSummary rejected = calculate(Collections.singletonList(item("Latte", 7.00, 2)),
                "WELCOME5", false, false);
        CheckoutSummary accepted = calculate(Collections.singletonList(item("Latte", 7.50, 2)),
                "WELCOME5", false, false);

        assertFalse(rejected.isPromoAccepted());
        assertEquals(0.00, rejected.getPromoDiscount(), DELTA);
        assertEquals(14.00, rejected.getFinalTotal(), DELTA);
        assertTrue(accepted.isPromoAccepted());
        assertEquals(5.00, accepted.getPromoDiscount(), DELTA);
        assertEquals(10.00, accepted.getFinalTotal(), DELTA);
    }

    @Test
    public void invalidPromoDoesNotAlterTotals() {
        CheckoutSummary summary = calculate(Collections.singletonList(item("Latte", 10.00, 2)),
                "NOTREAL", false, false);

        assertFalse(summary.isPromoAccepted());
        assertEquals(0.00, summary.getPromoDiscount(), DELTA);
        assertEquals(20.00, summary.getFinalTotal(), DELTA);
    }

    @Test
    public void fixedPromoCannotMakeTotalNegative() {
        PromoCode fixedTooLarge = new PromoCode("HUGE", PromoCode.DiscountType.FIXED, 50.00, 0.00);

        CheckoutSummary summary = CheckoutPriceCalculator.calculate(
                Collections.singletonList(item("Espresso", 3.00, 1)),
                fixedTooLarge,
                "HUGE",
                false,
                false);

        assertEquals(3.00, summary.getSubtotal(), DELTA);
        assertEquals(3.00, summary.getPromoDiscount(), DELTA);
        assertEquals(0.00, summary.getFinalTotal(), DELTA);
    }

    private CheckoutSummary calculate(java.util.List<CartItem> items, String promoCode,
                                      boolean loyaltyRequested, boolean loyaltyAvailable) {
        String normalizedCode = promoRepository.normalizeCode(promoCode);
        return CheckoutPriceCalculator.calculate(
                items,
                promoRepository.findPromoCode(normalizedCode),
                normalizedCode,
                loyaltyRequested,
                loyaltyAvailable);
    }

    private static CartItem item(String name, double unitPrice, int quantity) {
        return new CartItem(1, name, 0, "SINGLE", "SMALL", "NORMAL", quantity,
                unitPrice, unitPrice * quantity, "");
    }

    private static CartItem rewardItem(String name) {
        return new CartItem(1, name, 0, "SINGLE", "SMALL", "NORMAL", 1,
                0.00, 0.00, "", CartItem.REWARD_SOURCE_POINTS);
    }
}
