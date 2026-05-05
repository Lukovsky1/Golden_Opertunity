package com.GoldenOpportunity.Shop;

import java.util.List;

// js recieves events, delegates all to shopservice
public class ShopController {
    private ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    public List<ProductDescription> viewStore() {
        return shopService.viewStore();
    }


    public ProductDescription viewProductDetails(int productID) {
        return shopService.viewProductDetails(productID);
    }

    public String addProductToCart(int guestID, int productID, ShoppingCart shoppingCart) {
        return shopService.addProductToCart(guestID, productID, shoppingCart);
    }

    public String checkout(int guestID, PaymentDetails paymentDetails, ShoppingCart shoppingCart) {
        return shopService.checkout(guestID, paymentDetails, shoppingCart);
    }

    public String getAvailability (int productID) {
        return shopService.getAvailability(productID);
    }

    public int getStock(int productID) {
        return shopService.getStock(productID);
    }

    public Order getLastOrder() {
        return shopService.getLastOrder();
    }

    public String removeProductFromCart(int productID, ShoppingCart shoppingCart) {
        return shopService.removeProductFromCart(productID, shoppingCart);
    }
}
