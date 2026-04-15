package com.GoldenOpportunity.Shop;

import java.util.List;

public class ShopController {
    private ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    public List<String> viewStore() {
        return shopService.viewStore();
    }

    public String viewProductDetails(int productID) {
        return shopService.viewProductDetails(productID);
    }
}
