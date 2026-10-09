package com.example.ProductFin.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.ProductFin.Model.Product;
import com.example.ProductFin.Service.ProductCRUDService;

@RestController
@RequestMapping("/api/products")
public class ProductCrudController {

    private final ProductCRUDService service;

    public ProductCrudController(ProductCRUDService service) {
        this.service = service;
    }

    // GET /api/products - Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    // GET /api/products/{id} - Get one product
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = service.getProductById(id)
                .orElseThrow(() -> new com.example.ProductFin.Exception.ResourceNotFoundException(
                        "Product not found with ID: " + id));

        return ResponseEntity.ok(product);
    }

    // POST /api/products - Create a product
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = service.addProduct(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedProduct);
    }

    // POST /api/products/bulk - Create multiple products
    @PostMapping("/bulk")
    public ResponseEntity<List<Product>> addAllProducts(
            @RequestBody List<Product> products) {

        List<Product> savedProducts = service.addAllProducts(products);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedProducts);
    }

    // PUT /api/products/{id} - Update a product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable int id,
            @RequestBody Product product) {

        product.setProd_id(id);

        Product updatedProduct = service.updateProduct(product);

        return ResponseEntity.ok(updatedProduct);
    }

    // DELETE /api/products/{id} - Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        service.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}