package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.AuthenticationController;
import com.GoldenOpportunity.ReservationService;
import com.GoldenOpportunity.dbLogin.UserDao;

import java.sql.SQLException;
import java.util.List;

// js recieves events, delegates all to shopservice
public class ShopController {
    private ShopService shopService;
    private AuthenticationController authenticationController;
    private ReservationService reservationService;
    private UserDao userDao;

    public ShopController(ShopService shopService,
                          AuthenticationController authenticationController,
                          ReservationService reservationService, UserDao userDao) {
        this.shopService = shopService;
        this.authenticationController = authenticationController;
        this.reservationService = reservationService;
        this.userDao = userDao;
    }

    public List<String> viewStore() {
        return shopService.viewStore();
    }

    public String viewProductDetails(int productID) {
        return shopService.viewProductDetails(productID);
    }

    public String addProductToCart(int guestID, int productID, ShoppingCart shoppingCart)  {
        try {
            if (!userDao.isAuthenticated(guestID)) {
                return "guest is not authenticated";
            }

            if (!reservationService.hasValidReservation(guestID)) {
                return "guest does not have a valid reservation";
            }

            return shopService.addProductToCart(guestID, productID, shoppingCart);
        } catch (SQLException e) {
            System.err.println("Error from reading from database" + e.getMessage());
        }
        return "Error occurred adding product to cart";
    }

    public String checkout(int guestID, String paymentDetails, ShoppingCart shoppingCart) throws SQLException {
        return shopService.checkout(guestID, paymentDetails, shoppingCart);
    }
}
