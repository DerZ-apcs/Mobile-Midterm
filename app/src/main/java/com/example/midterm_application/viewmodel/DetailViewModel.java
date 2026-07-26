package com.example.midterm_application.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.midterm_application.utils.PriceCalculator;
import com.example.midterm_application.utils.PriceCalculator.Ice;
import com.example.midterm_application.utils.PriceCalculator.Shot;
import com.example.midterm_application.utils.PriceCalculator.Size;

public class DetailViewModel extends ViewModel {
    public static final int MAX_NOTE_LENGTH = 120;

    private int quantity = PriceCalculator.MIN_QUANTITY;
    private Shot selectedShot = Shot.SINGLE;
    private Size selectedSize = Size.SMALL;
    private Ice selectedIce = Ice.NORMAL;
    private String note = "";

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = PriceCalculator.normalizeQuantity(quantity);
    }

    public Shot getSelectedShot() {
        return selectedShot;
    }

    public void setSelectedShot(Shot selectedShot) {
        if (selectedShot != null) {
            this.selectedShot = selectedShot;
        }
    }

    public Size getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(Size selectedSize) {
        if (selectedSize != null) {
            this.selectedSize = selectedSize;
        }
    }

    public Ice getSelectedIce() {
        return selectedIce;
    }

    public void setSelectedIce(Ice selectedIce) {
        if (selectedIce != null) {
            this.selectedIce = selectedIce;
        }
    }

    public double calculateTotal(double basePrice) {
        return PriceCalculator.calculateTotal(basePrice, selectedShot, selectedSize, selectedIce, quantity);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        String normalized = note == null ? "" : note.trim();
        if (normalized.length() > MAX_NOTE_LENGTH) {
            normalized = normalized.substring(0, MAX_NOTE_LENGTH);
        }
        this.note = normalized;
    }
}
