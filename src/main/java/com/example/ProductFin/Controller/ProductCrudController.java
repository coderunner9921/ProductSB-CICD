package com.example.ProductFin.Controller;

import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.ProductFin.Service.ProductCRUDService;
import com.example.ProductFin.Model.Product;

@RestController
public class ProductCrudController {
	private ProductCRUDService service;

	public ProductCrudController(ProductCRUDService service) {
		super();
		this.service = service;
	}
	
	@PostMapping("/addProduct")
    public void addProduct(@RequestBody Product prod) {
        service.addProduct(prod);
    }
    
    @PostMapping("/addAllProducts")
    public void addAllProducts(@RequestBody List<Product> prodList) {
        service.addAllProducts(prodList);
    }
    
    @GetMapping("/getAllProducts")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/getProductById/{id}")
    public Optional<Product> getProductById(@PathVariable int id) {
        return service.getProductById(id);
    }

    @PostMapping("/updateProduct")
    public void updateProduct(@RequestBody Product prod) {
        service.updateProduct(prod);
    }

    @PostMapping("/deleteProduct/{id}")
    public void deleteProduct(@PathVariable int id) {
        service.deleteProduct(id);
    }
}
