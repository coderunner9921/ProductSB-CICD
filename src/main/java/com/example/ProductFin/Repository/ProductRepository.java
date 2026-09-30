package com.example.ProductFin.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ProductFin.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {}
