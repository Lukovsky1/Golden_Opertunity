package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.PaymentMethod;
import com.GoldenOpportunity.ReservationService;
import com.GoldenOpportunity.Roles.Clerk;
import com.GoldenOpportunity.dbLogin.UserDao;

import java.sql.SQLException;
import java.util.List;

public class ShopService {
    private Shop shop;
    private PaymentMethod paymentMethod;
    private Clerk clerk;
    private Order lastOrder;
    private ReservationService reservationService;
    private UserDao userDao;

    public ShopService(Shop shop, PaymentMethod paymentMethod, Clerk clerk,
                       ReservationService reservationService, UserDao userDao) {
        this.shop = shop;
        this.paymentMethod = paymentMethod;
        this.clerk = clerk;
        this.reservationService = reservationService;
        this.userDao = userDao;
    }

    // views all available products, the main store page
    public List<ProductDescription> viewStore() {
        return shop.getAvailableProducts();
    }

    // more in depth
    public ProductDescription viewProductDetails(int productID) {
        return shop.findProduct(productID);
    }

    /*public String addProductToCart(int guestID, int productID, ShoppingCart shoppingCart) {
        ProductDescription productDescription = shop.findProduct(productID);

        if (productDescription == null) {
            return "product not found";
        }

        int stock = shop.getStock(productID);
        int quantityInCart = shoppingCart.getQuantity(productID);

        if (stock <= 0) {
            return "product is out of stock";
        }

        if (quantityInCart >= stock) {
            return "not enough stock";
        }

        shoppingCart.addProductToCart(productDescription);
        return "updatedCart";
    }*/
    // adds a product to cart after checking authentication, reservation, product, and stock
    public String addProductToCart(int guestID, int productID, ShoppingCart shoppingCart) {
        try {
            if (!userDao.isAuthenticated(guestID)) {
                return "guest is not authenticated";
            }

            if (!reservationService.hasValidReservation(guestID)) {
                return "guest does not have a valid reservation";
            }
        } catch (SQLException e) {
            System.err.println("Error from reading from database" + e.getMessage());
            return "Error occurred adding product to cart";
        }

        ProductDescription productDescription = shop.findProduct(productID);

        if (productDescription == null) {
            return "product not found";
        }

        int stock = shop.getStock(productID);
        int quantityInCart = shoppingCart.getQuantity(productID);

        if (stock <= 0) {
            return "product is out of stock";
        }

        if (quantityInCart >= stock) {
            return "not enough stock";
        }

        shoppingCart.addProductToCart(productDescription);
        return "updatedCart";
    }


    // simple checkout
    public String checkout(int guestID, PaymentDetails paymentDetails, ShoppingCart shoppingCart) {
        double total = shoppingCart.calculateTotal();

        if (total <= 0) {
            return "cart is empty";
        }

        List<ProductDescription> uniqueItems = shoppingCart.getUniqueCartItems();

        for (ProductDescription productDescription : uniqueItems) {
            int productID = productDescription.getProductID();
            int quantity = shoppingCart.getQuantity(productID);

            if (shop.getStock(productID) < quantity) {
                return "not enough stock";
            }
        }

        boolean paymentApproved = paymentMethod.submitPayment(guestID, paymentDetails);

        if (!paymentApproved) {
            return "payment failed";
        }

        Order order = new Order();
        order.createOrder(shoppingCart.getCartItems(), guestID, total);

        for (ProductDescription productDescription : uniqueItems) {
            int productID = productDescription.getProductID();
            int quantity = shoppingCart.getQuantity(productID);

            boolean stockReduced = shop.reduceStock(productID, quantity);

            if (!stockReduced) {
                return "not enough stock";
            }
        }

        lastOrder = order;

        shoppingCart.clearCart();
        clerk.notifyOrder(order);

        return "receipt";
    }

    public String getAvailability(int productID) {
        return shop.getAvailability(productID);
    }

    public int getStock(int productID) {
        return shop.getStock(productID);
    }

    public Order getLastOrder() {
        return lastOrder;
    }

    public String removeProductFromCart(int productID, ShoppingCart shoppingCart) {
        boolean removed = shoppingCart.removeProductByID(productID);

        if (removed) {
            return "updatedCart";
        }

        return "product not found in cart";
    }
}
