package com.example.ProductFin.Model;
 
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