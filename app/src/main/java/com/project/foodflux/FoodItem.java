package com.project.foodflux;

public class FoodItem {
    private String name;
    private int quantity;
    private String expiryDate;

    public FoodItem() {
        // Default constructor required for Firebase
    }

    public FoodItem(String name, int quantity, String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
