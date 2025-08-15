package com.crudapp.controller;

import com.crudapp.model.Product;
import com.crudapp.repository.ProductRepository;
import com.crudapp.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("products", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product product = productService.getProductById(id);
            if (product != null) {
                response.put("success", true);
                response.put("product", product);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Invalid product ID");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam String price,
            @RequestParam(required = false) String quantity,
            @RequestParam(required = false) String category,
            HttpSession session) {
        
        // Check if user is authenticated
        if (session == null || session.getAttribute("user") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        Map<String, Object> response = new HashMap<>();
        
        if (name == null || name.trim().isEmpty() || price == null || price.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Name and price are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            BigDecimal priceValue = new BigDecimal(price.trim());
            Integer quantityValue = quantity != null && !quantity.trim().isEmpty() ? 
                Integer.parseInt(quantity.trim()) : 0;
            
            Product product = new Product(
                name.trim(),
                description != null ? description.trim() : "",
                priceValue,
                quantityValue,
                category != null ? category.trim() : ""
            );
            
            Product savedProduct = productService.saveProduct(product);
            response.put("success", true);
            response.put("message", "Product created successfully");
            response.put("product", savedProduct);
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Invalid price or quantity format");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create product");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String quantity,
            @RequestParam(required = false) String category,
            HttpSession session) {
        
        // Check if user is authenticated
        if (session == null || session.getAttribute("user") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                response.put("success", false);
                response.put("message", "Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            if (name != null && !name.trim().isEmpty()) {
                existingProduct.setName(name.trim());
            }
            
            if (description != null) {
                existingProduct.setDescription(description.trim());
            }
            
            if (price != null && !price.trim().isEmpty()) {
                try {
                    BigDecimal priceValue = new BigDecimal(price.trim());
                    existingProduct.setPrice(priceValue);
                } catch (NumberFormatException e) {
                    response.put("success", false);
                    response.put("message", "Invalid price format");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            if (quantity != null && !quantity.trim().isEmpty()) {
                try {
                    Integer quantityValue = Integer.parseInt(quantity.trim());
                    existingProduct.setQuantity(quantityValue);
                } catch (NumberFormatException e) {
                    response.put("success", false);
                    response.put("message", "Invalid quantity format");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            if (category != null) {
                existingProduct.setCategory(category.trim());
            }
            
            Product updatedProduct = productService.updateProduct(existingProduct);
            response.put("success", true);
            response.put("message", "Product updated successfully");
            response.put("product", updatedProduct);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update product");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpSession session) {
        // Check if user is authenticated
        if (session == null || session.getAttribute("user") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Product deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Product not found or failed to delete");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete product");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}