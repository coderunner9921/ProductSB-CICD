package com.example.ProductFin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.Mockito;
import com.example.ProductFin.Exception.ResourceNotFoundException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.ProductFin.Model.Product;
import com.example.ProductFin.Repository.ProductRepository;
import com.example.ProductFin.Service.ProductCRUDService;

@ExtendWith(MockitoExtension.class)
class ProductCRUDServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductCRUDService service;

    @Test
    void getAllProducts_shouldReturnProducts() {

        Product p1 = new Product(1, "Laptop", 50000);
        Product p2 = new Product(2, "Mouse", 1000);

        List<Product> products = Arrays.asList(p1, p2);

        when(repo.findAll()).thenReturn(products);

        List<Product> result = service.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getProd_name());
        assertEquals("Mouse", result.get(1).getProd_name());

        verify(repo, times(1)).findAll();
    }

    @Test
    void addProduct_shouldSaveProduct() {

        Product product = new Product(1, "Laptop", 50000);

        service.addProduct(product);

        verify(repo, times(1)).save(product);
    }

    @Test
    void addAllProducts_shouldSaveAllProducts() {

        Product p1 = new Product(1, "Laptop", 50000);
        Product p2 = new Product(2, "Mouse", 1000);

        List<Product> products = Arrays.asList(p1, p2);

        service.addAllProducts(products);

        verify(repo, times(1)).saveAll(products);
    }

    @Test
    void getProductById_shouldReturnProduct() {

        Product product = new Product(1, "Laptop", 50000);

        when(repo.findById(1)).thenReturn(Optional.of(product));

        Product result = service.getProductById(1).orElse(null);

        assertEquals("Laptop", result.getProd_name());
        assertEquals(50000, result.getPrice());

        verify(repo, times(1)).findById(1);
    }

	@Test
	void updateProduct_shouldSaveProduct() {
	
	    Product product = new Product(1, "Updated Laptop", 60000);
	
	    when(repo.existsById(1)).thenReturn(true);
	
	    service.updateProduct(product);
	
	    verify(repo, times(1)).existsById(1);
	    verify(repo, times(1)).save(product);
	}
	
	@Test
	void updateProduct_shouldThrowExceptionWhenProductDoesNotExist() {

	    Product product = new Product(99, "Unknown Product", 500);

	    when(repo.existsById(99)).thenReturn(false);

	    assertThrows(
	        ResourceNotFoundException.class,
	        () -> service.updateProduct(product)
	    );

	    verify(repo, times(1)).existsById(99);
	}


	@Test
	void deleteProduct_shouldDeleteProduct() {
	
	    when(repo.existsById(1)).thenReturn(true);
	
	    service.deleteProduct(1);
	
	    verify(repo, times(1)).existsById(1);
	    verify(repo, times(1)).deleteById(1);
	}
	
	@Test
	void deleteProduct_shouldThrowExceptionWhenProductDoesNotExist() {

	    when(repo.existsById(99)).thenReturn(false);

	    assertThrows(
	        ResourceNotFoundException.class,
	        () -> service.deleteProduct(99)
	    );

	    verify(repo, times(1)).existsById(99);
	}
}