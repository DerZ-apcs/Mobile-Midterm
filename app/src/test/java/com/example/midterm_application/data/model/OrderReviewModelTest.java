package com.example.midterm_application.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OrderReviewModelTest {
    @Test
    public void ratingValidationAcceptsOneThroughFive() {
        assertTrue(OrderReview.isValidRating(1));
        assertTrue(OrderReview.isValidRating(5));
    }

    @Test
    public void ratingValidationRejectsOutOfRangeValues() {
        assertFalse(OrderReview.isValidRating(0));
        assertFalse(OrderReview.isValidRating(6));
    }

    @Test
    public void reviewPreservesOrderRatingCommentAndTimestamps() {
        OrderReview review = new OrderReview(12L, 4, "Great coffee", 1000L, 2000L);

        assertEquals(12L, review.getOrderId());
        assertEquals(4, review.getRating());
        assertEquals("Great coffee", review.getComment());
        assertEquals(1000L, review.getCreatedAt());
        assertEquals(2000L, review.getUpdatedAt());
    }

    @Test
    public void reviewAllowsEmptyComment() {
        OrderReview review = new OrderReview(12L, 5, null, 1000L, 1000L);

        assertEquals("", review.getComment());
    }
}
