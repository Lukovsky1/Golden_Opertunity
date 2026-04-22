package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.PaymentMethod;
import com.GoldenOpportunity.Roles.Clerk;

import java.util.ArrayList;
import java.util.List;

public class ShopService {
    private Shop shop;
    private PaymentMethod paymentMethod;
    private Clerk clerk;

    public ShopService(Shop shop, PaymentMethod paymentMethod, Clerk clerk) {
        this.shop = shop;
        this.paymentMethod = paymentMethod;
        this.clerk = clerk;
    }

    // views all available products, the main store page
    public List<String> viewStore() {
        List<ProductDescription> availableProductDescriptions = shop.getAvailableProducts();
        List<String> productSummaryList = new ArrayList<>();

        for (ProductDescription productDescription : availableProductDescriptions) {
            productSummaryList.add(productDescription.getSummary());
        }

        return productSummaryList;
    }

    // more in depth
    public String viewProductDetails(int productID) {
        ProductDescription productDescription = shop.findProduct(productID);

        if (productDescription == null) {
            return "product not found";
        }

        String availability = shop.getAvailability(productID);
        return productDescription.getDetails() + ", Availability: " + availability;
    }

    // adds a product to cart, assumes controller already handled auth and reservation checks
    public String addProductToCart(int guestID, int productID, ShoppingCart shoppingCart) {
        ProductDescription productDescription = shop.findProduct(productID);

        if (productDescription == null) {
            return "product not found";
        }

        if (!shop.isInStock(productID)) {
            return "product is out of stock";
        }

        shoppingCart.addProductToCart(productDescription);
        return "updatedCart";
    }

    // simple checkout
    public String checkout(int guestID, String paymentDetails, ShoppingCart shoppingCart) {
        double total = shoppingCart.calculateTotal();

        if (total <= 0) {
            return "cart is empty";
        }

        boolean paymentApproved = paymentMethod.submitPayment(guestID, paymentDetails);

        if (!paymentApproved) {
            return "payment failed";
        }

        Order order = new Order();
        order.createOrder(shoppingCart.getCartItems(), guestID, total);

        shoppingCart.clearCart();
        clerk.notifyOrder(order);

        return "receipt";
    }
}
