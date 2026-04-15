package com.GoldenOpportunity.Shop;

import java.util.ArrayList;
import java.util.List;

public class ShopService {
    private Shop shop;

    public ShopService(Shop shop) {
        this.shop = shop;
    }

    public List<String> viewStore() {
        List<Product> availableProducts = shop.getAvailableProducts();
        List<String> productSummaryList = new ArrayList<>();

        for (Product product : availableProducts) {
            productSummaryList.add(product.getSummary());
        }

        return productSummaryList;
    }

    public String viewProductDetails(int productID) {
        Product product = shop.findProduct(productID);

        if (product == null) {
            return "product not found";
        }

        return product.getDetails();
    }
}
