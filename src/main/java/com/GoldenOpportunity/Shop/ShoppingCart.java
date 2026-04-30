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

    public int getQuantity(int productID) {
        int quantity = 0;

        for (ProductDescription productDescription : cartItems) {
            if (productDescription.getProductID() == productID) {
                quantity++;
            }
        }

        return quantity;
    }

    public List<ProductDescription> getUniqueCartItems() {
        List<ProductDescription> uniqueItems = new ArrayList<>();

        for (ProductDescription productDescription : cartItems) {
            boolean alreadyAdded = false;

            for (ProductDescription uniqueProduct : uniqueItems) {
                if (uniqueProduct.getProductID() == productDescription.getProductID()) {
                    alreadyAdded = true;
                    break;
                }
            }

            if (!alreadyAdded) {
                uniqueItems.add(productDescription);
            }
        }

        return uniqueItems;
    }

    public boolean removeProductByID(int productID) {
        for (ProductDescription productDescription : cartItems) {
            if (productDescription.getProductID() == productID) {
                cartItems.remove(productDescription);
                return true;
            }
        }

        return false;
    }
}
