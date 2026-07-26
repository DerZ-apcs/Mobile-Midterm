package com.example.midterm_application.utils;

import static com.example.midterm_application.utils.PriceCalculator.Ice.NORMAL;
import static com.example.midterm_application.utils.PriceCalculator.Shot.DOUBLE;
import static com.example.midterm_application.utils.PriceCalculator.Shot.SINGLE;
import static com.example.midterm_application.utils.PriceCalculator.Size.LARGE;
import static com.example.midterm_application.utils.PriceCalculator.Size.MEDIUM;
import static com.example.midterm_application.utils.PriceCalculator.Size.SMALL;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PriceCalculatorTest {
    private static final double DELTA = 0.001;

    @Test
    public void calculatesRequiredBasePriceScenarios() {
        assertEquals(3.00, PriceCalculator.calculateTotal(3.00, SINGLE, SMALL, NORMAL, 1), DELTA);
        assertEquals(3.50, PriceCalculator.calculateTotal(3.00, DOUBLE, SMALL, NORMAL, 1), DELTA);
        assertEquals(3.50, PriceCalculator.calculateTotal(3.00, SINGLE, MEDIUM, NORMAL, 1), DELTA);
        assertEquals(4.00, PriceCalculator.calculateTotal(3.00, SINGLE, LARGE, NORMAL, 1), DELTA);
        assertEquals(4.50, PriceCalculator.calculateTotal(3.00, DOUBLE, LARGE, NORMAL, 1), DELTA);
        assertEquals(9.00, PriceCalculator.calculateTotal(3.00, DOUBLE, LARGE, NORMAL, 2), DELTA);
    }

    @Test
    public void calculatesWithDifferentBasePrice() {
        assertEquals(11.00, PriceCalculator.calculateTotal(4.50, DOUBLE, MEDIUM, NORMAL, 2), DELTA);
    }
}
