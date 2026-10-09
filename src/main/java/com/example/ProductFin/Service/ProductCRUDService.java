
package com.example.ProductFin.Service;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ProductFin.Exception.ResourceNotFoundException;
import com.example.ProductFin.Model.Product;
import com.example.ProductFin.Repository.ProductRepository;

@Service
public class ProductCRUDService {

    private final ProductRepository repo;

    public ProductCRUDService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product addProduct(Product prod) {
        return repo.save(prod);
    }

    public List<Product> addAllProducts(List<Product> prodList) {
        return repo.saveAll(prodList);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Optional<Product> getProductById(int id) {
        return repo.findById(id);
    }

    @Transactional
    public Product updateProduct(Product prod) {
        Integer id = prod.getProd_id();

        if (id == null || !repo.existsById(id)) {
            throw new ResourceNotFoundException(
                "Product not found with ID: " + id
            );
        }
        return repo.save(prod);
    }

    @Transactional
    public void deleteProduct(int id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException(
                "Product not found with ID: " + id
            );
        }
        repo.deleteById(id);
    }
}