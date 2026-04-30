package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.*;
import com.GoldenOpportunity.Roles.Clerk;
import com.GoldenOpportunity.dbLogin.GuestReservationDao;

import java.sql.SQLException;
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
    public String checkout(int guestID, String paymentDetails, ShoppingCart shoppingCart) throws SQLException {
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

        //Add to receipt
        GuestReservationDao guest = new GuestReservationDao();
        List<String> reservations = guest.findGuestReservations(guestID);
        SearchController searchController = new SearchController(new RoomService(), new ReservationService());
        for(String resID: reservations){
            if(searchController.getResService().findReservation(resID).isCheckedIn()){
                for(ProductDescription p: shoppingCart.getCartItems()){
                    String name = p.getName();
                    double price = p.getPrice();
                    searchController.getResService().findReservation(resID).getReceipt().addShopItemToBill(name, price);
                }
                break;
            }
        }

        shoppingCart.clearCart();
        clerk.notifyOrder(order);

        return "receipt";
    }
}
