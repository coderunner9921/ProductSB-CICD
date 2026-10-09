
package com.example.ProductFin.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ProductFin.Model.Product;

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
}