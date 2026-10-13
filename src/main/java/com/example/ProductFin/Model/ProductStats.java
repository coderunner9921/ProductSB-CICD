package com.example.ProductFin.Model;
// Catalogue-wide figures shown on the Products page.
// averagePrice / highestPrice / lowestPrice are null when the catalogue is empty,
 
public class ProductStats {

    private final long totalProducts;
    private final Double averagePrice;
    private final Double highestPrice;
    private final Double lowestPrice;

    public ProductStats(Long totalProducts, Double averagePrice, Double highestPrice, Double lowestPrice) {
        this.totalProducts = totalProducts == null ? 0L : totalProducts;
        this.averagePrice = averagePrice;
        this.highestPrice = highestPrice;
        this.lowestPrice = lowestPrice;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public Double getHighestPrice() {
        return highestPrice;
    }

    public Double getLowestPrice() {
        return lowestPrice;
    }
}