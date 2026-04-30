package com.GoldenOpportunity.Shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<ProductDescription> cartItems;

    public ShoppingCart() {
        cartItems = new ArrayList<>();
    }

    public void addProductToCart(ProductDescription productDescription) {
        cartItems.add(productDescription);
    }

    public List<ProductDescription> getCartItems() {
        return cartItems;
    }

    public double calculateTotal() {
        double total = 0.0;

        for (ProductDescription productDescription : cartItems) {
            total += productDescription.getPrice();
        }

        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }
}
