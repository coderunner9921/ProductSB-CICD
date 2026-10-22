package com.example.ProductFin.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.ProductFin.Model.Product;
import com.example.ProductFin.Model.ProductStats;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("""
        SELECT p FROM Product p
        WHERE (:search IS NULL OR
               LOWER(p.prod_name) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Product> searchProducts(
            @Param("search") String search,
            Pageable pageable
    );

    // Catalogue-wide figures for the dashboard cards: ONE aggregate query, whole table.
    @Query("""
        SELECT new com.example.ProductFin.Model.ProductStats(
            COUNT(p), AVG(p.price), MAX(p.price), MIN(p.price))
        FROM Product p
        """)
    ProductStats getProductStats();
}