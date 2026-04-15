package com.GoldenOpportunity.Shop;

import java.util.List;

public class Shop {
    private ProductRepo productRepo;

    public Shop(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAvailableProducts() {
        return productRepo.getAvailableProducts();
    }

    public Product findProduct(int productID) {
        return productRepo.findProductById(productID);
    }
}
