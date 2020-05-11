package com.example.cookbookapp.Models;

import androidx.annotation.NonNull;

public class Measurement {

    private String ingredientTitle;

    private String quantity;

    private String consistency;

    public Measurement(
            String ingredientTitle,
            String quantity,
            String consistency
    ) {
        this.ingredientTitle = ingredientTitle;
        this.quantity = quantity;
        this.consistency = consistency;
    }

    public String getIngredientTitle() {
        return ingredientTitle;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getConsistency() {
        return consistency;
    }

    @NonNull
    @Override
    public String toString() {
        return ingredientTitle + "-" + quantity + "-" + consistency;
    }
}
