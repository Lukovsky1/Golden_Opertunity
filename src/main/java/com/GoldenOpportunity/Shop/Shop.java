package com.GoldenOpportunity.Shop;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Shop {
    private List<Product> products;
    private static double profit;

    Shop(String filename) throws IOException {
        products = shopCSVParse(filename);
    }

    private List<Product> shopCSVParse(String filename) throws IOException {
        Scanner scanner = new Scanner(Path.of("src/main/resources/testProductData.csv"));
        scanner.nextLine();
        List<Product> p = new ArrayList<>();

        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] data = line.split(",");

            p.add(new Product(Double.parseDouble(data[0]),Integer.parseInt(data[1]),data[2],Integer.parseInt(data[3]),data[4]));
        }

        return p;
    }

    public void setProducts(List<Product> products) {this.products = products;}

    public List<Product> getProducts() {return products;}
}
