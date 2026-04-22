package com.GoldenOpportunity.Shop;

import com.GoldenOpportunity.AuthenticationController;
import com.GoldenOpportunity.PaymentMethod;
import com.GoldenOpportunity.ReservationService;
import com.GoldenOpportunity.Roles.Clerk;

import java.nio.file.Path;
import java.util.List;

// im seperating the UI from backend js to test my logic
public class ShopBackendTest {
    public static void main(String[] args) {
        // make sure the db/table/data exist first
        ShopDBInitializer.initializeDatabase();

        ProductRepo productRepo = new ProductRepo("jdbc:sqlite:src/main/resources/shop.db");
        Shop shop = new Shop(productRepo);

        System.out.println("=== testing productrepo / shop ===");
        List<ProductDescription> allProductDescriptions = productRepo.getAllProductDescriptions();
        System.out.println("total products loaded: " + allProductDescriptions.size());

        for (ProductDescription productDescription : allProductDescriptions) {
            System.out.println(productDescription.getSummary());
        }

        System.out.println();
        System.out.println("=== testing shop methods ===");
        System.out.println("available products count: " + shop.getAvailableProducts().size());

        ProductDescription productDescription1 = shop.findProduct(1);
        if (productDescription1 != null) {
            System.out.println("found product 1:");
            System.out.println(productDescription1.getDetails());
        } else {
            System.out.println("product 1 not found");
        }

        System.out.println();
        System.out.println("=== testing service/controller ===");

        AuthenticationController authenticationController = new AuthenticationController();
        ReservationService reservationService =
                new ReservationService(Path.of("src/main/resources/testReservationData1.csv"));
        PaymentMethod paymentMethod = new PaymentMethod();

        Clerk clerk = new Clerk(2, "clerk1", "clerkpass", "clerk@golden.com");

        ShopService shopService = new ShopService(shop, paymentMethod, clerk);

        ShopController shopController = new ShopController(shopService, authenticationController, reservationService);
        ShoppingCart shoppingCart = new ShoppingCart();

        System.out.println("view store:");
        List<String> storeView = shopController.viewStore();
        for (String item : storeView) {
            System.out.println(item);
        }

        System.out.println();
        System.out.println("view product details for product 1:");
        System.out.println(shopController.viewProductDetails(1));

        System.out.println();
        System.out.println("add product 1 to cart for guest 3:");
        System.out.println(shopController.addProductToCart(3, 1, shoppingCart));

        System.out.println("cart total after add:");
        System.out.println(shoppingCart.calculateTotal());

        System.out.println();
        System.out.println("checkout:");
        System.out.println(shopController.checkout(3, "test-payment-info", shoppingCart));

        System.out.println("cart total after checkout:");
        System.out.println(shoppingCart.calculateTotal());
    }
}
