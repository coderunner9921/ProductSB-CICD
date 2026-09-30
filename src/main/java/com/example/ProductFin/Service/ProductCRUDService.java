package com.example.ProductFin.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.ProductFin.Repository.ProductRepository;
import com.example.ProductFin.Model.Product;

@Service
public class ProductCRUDService {
	ProductRepository repo;
	
	public ProductCRUDService(ProductRepository repo) {
		super();
		this.repo = repo;
	}
	public void addProduct(Product prod) {
		repo.save(prod);
	}
	public void addAllProducts(List<Product> prodList) {
		repo.saveAll(prodList);
	}
	public List<Product> getAllProducts() {
		return repo.findAll();
	}
	public Optional<Product> getProductById(int id) {
		return repo.findById(id);
	}
	public void updateProduct(Product prod) {
		repo.save(prod);
	}
	public void deleteProduct(int id) {
		repo.deleteById(id);
	}
}
