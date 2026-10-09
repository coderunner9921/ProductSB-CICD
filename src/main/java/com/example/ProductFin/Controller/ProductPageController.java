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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.ProductFin.Exception.ResourceNotFoundException;

@Controller
public class ProductPageController {

    private final ProductCRUDService service;

    public ProductPageController(ProductCRUDService service) {
        this.service = service;
    }


	@GetMapping("/products")
	public String showProducts(
	        @RequestParam(defaultValue = "") String search,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "prod_id") String sortBy,
	        @RequestParam(defaultValue = "asc") String sortDir,
	        Model model) {
	
	    // Allow only supported page sizes.
	    if (size != 5 && size != 10 && size != 20) {
	        size = 10;
	    }
	
	    // Prevent negative page numbers.
	    page = Math.max(0, page);
	
	    // Allow only known entity properties for sorting.
	    if (!List.of("prod_id", "prod_name", "price").contains(sortBy)) {
	        sortBy = "prod_id";
	    }
	
	    sortDir = "desc".equalsIgnoreCase(sortDir) ? "desc" : "asc";
	
	    Sort sort = "desc".equals(sortDir)
	            ? Sort.by(sortBy).descending()
	            : Sort.by(sortBy).ascending();
	
	    Pageable pageable = PageRequest.of(page, size, sort);
	    Page<Product> productPage = service.searchProducts(search, pageable);
	
	    // If the requested page is beyond the available results, show the last page.
	    if (productPage.getTotalPages() > 0
	            && page >= productPage.getTotalPages()) {
	
	        page = productPage.getTotalPages() - 1;
	        pageable = PageRequest.of(page, size, sort);
	        productPage = service.searchProducts(search, pageable);
	    }
	
	    model.addAttribute("products", productPage.getContent());
	    model.addAttribute("product", new Product());
	    model.addAttribute("productPage", productPage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("pageSize", size);
	    model.addAttribute("search", search);
	    model.addAttribute("sortBy", sortBy);
	    model.addAttribute("sortDir", sortDir);
	
	    return "products";
	}

    // CREATE - Add product
	@PostMapping("/products/add")
	public String addProduct(@ModelAttribute Product product) {
	    service.addProduct(product);
	    return "redirect:/products?success=added";
	}

    // READ - Get product for editing
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable int id, Model model) {
	    Product product = service.getProductById(id)
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Product not found with ID: " + id));
	
	    // Populate all the attributes needed by the dashboard.
	    Page<Product> productPage = service.searchProducts(
	            "", PageRequest.of(0, 10, Sort.by("prod_id").ascending()));
	
	    model.addAttribute("products", productPage.getContent());
	    model.addAttribute("product", new Product());
	    model.addAttribute("editProduct", product);
	    model.addAttribute("productPage", productPage);
	    model.addAttribute("currentPage", 0);
	    model.addAttribute("pageSize", 10);
	    model.addAttribute("search", "");
	    model.addAttribute("sortBy", "prod_id");
	    model.addAttribute("sortDir", "asc");
	
	    return "products";
	}

    // UPDATE - Update product
    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product product) {
        service.updateProduct(product);
        return "redirect:/products?success=updated";
    }

    // DELETE - Delete product
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable int id) {
        try {
            service.deleteProduct(id);
            return "redirect:/products?success=deleted";
        } catch (ResourceNotFoundException ex) {
            return "redirect:/products?error=Product+not+found";
        }
    } 
}