package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.AuthenticationController;
import com.GoldenOpportunity.ReservationService;

import java.util.List;

// js recieves events, delegates all to shopservice
public class ShopController {
    private ShopService shopService;
    private AuthenticationController authenticationController;
    private ReservationService reservationService;

    public ShopController(ShopService shopService,
                          AuthenticationController authenticationController,
                          ReservationService reservationService) {
        this.shopService = shopService;
        this.authenticationController = authenticationController;
        this.reservationService = reservationService;
    }

    public List<String> viewStore() {
        return shopService.viewStore();
    }

    public String viewProductDetails(int productID) {
        return shopService.viewProductDetails(productID);
    }

    public String addProductToCart(int guestID, int productID, ShoppingCart shoppingCart) {
        /* isAuthenticated needs to be updated
        if (!authenticationController.isAuthenticated(guestID)) {
            return "guest is not authenticated";
        }
        */
        if (!reservationService.hasValidReservation(guestID)) {
            return "guest does not have a valid reservation";
        }

        return shopService.addProductToCart(guestID, productID, shoppingCart);
    }

    public String checkout(int guestID, String paymentDetails, ShoppingCart shoppingCart) {
        return shopService.checkout(guestID, paymentDetails, shoppingCart);
    }
}
