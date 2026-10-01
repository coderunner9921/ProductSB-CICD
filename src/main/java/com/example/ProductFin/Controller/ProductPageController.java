package com.example.ProductFin.Controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.ProductFin.Model.Product;
import com.example.ProductFin.Service.ProductCRUDService;

@Controller
public class ProductPageController {

    private final ProductCRUDService service;

    public ProductPageController(ProductCRUDService service) {
        this.service = service;
    }

    // READ - Display all products
    @GetMapping("/products")
    public String showProducts(Model model) {

        List<Product> products = service.getAllProducts();

        model.addAttribute("products", products);
        model.addAttribute("product", new Product());

        return "products";
    }

    // CREATE - Add product
    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product) {

        service.addProduct(product);

        return "redirect:/products";
    }

    // READ - Get product for editing
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable int id, Model model) {

        Product product = service.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("products", service.getAllProducts());
        model.addAttribute("editProduct", product);

        return "products";
    }

    // UPDATE - Update product
    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product product) {

        service.updateProduct(product);

        return "redirect:/products";
    }

    // DELETE - Delete product
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable int id) {

        service.deleteProduct(id);

        return "redirect:/products";
    }
}