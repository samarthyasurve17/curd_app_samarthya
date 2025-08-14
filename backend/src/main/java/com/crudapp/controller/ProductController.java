package com.crudapp.controller;

import com.crudapp.dao.ProductDAO;
import com.crudapp.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductDAO productDAO;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("products", products);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving products: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Product product = productDAO.getProductById(id);
            if (product != null) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("success", true);
                responseMap.put("product", product);
                return ResponseEntity.ok(responseMap);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Product not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving product: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestParam String name,
                                          @RequestParam String description,
                                          @RequestParam BigDecimal price,
                                          @RequestParam Integer quantity,
                                          @RequestParam String category,
                                          HttpSession session) {
        
        // Check if user is authenticated
        if (session.getAttribute("user") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authentication required");
            return ResponseEntity.status(401).body(errorResponse);
        }
        
        try {
            Product newProduct = new Product(name, description, price, quantity, category);
            Product savedProduct = productDAO.createProduct(newProduct);
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("message", "Product created successfully");
            responseMap.put("product", savedProduct);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error creating product: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                          @RequestParam String name,
                                          @RequestParam String description,
                                          @RequestParam BigDecimal price,
                                          @RequestParam Integer quantity,
                                          @RequestParam String category,
                                          HttpSession session) {
        
        // Check if user is authenticated
        if (session.getAttribute("user") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authentication required");
            return ResponseEntity.status(401).body(errorResponse);
        }
        
        try {
            Product existingProduct = productDAO.getProductById(id);
            if (existingProduct == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Product not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            existingProduct.setName(name);
            existingProduct.setDescription(description);
            existingProduct.setPrice(price);
            existingProduct.setQuantity(quantity);
            existingProduct.setCategory(category);
            
            Product updatedProduct = productDAO.updateProduct(existingProduct);
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("message", "Product updated successfully");
            responseMap.put("product", updatedProduct);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error updating product: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpSession session) {
        // Check if user is authenticated
        if (session.getAttribute("user") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authentication required");
            return ResponseEntity.status(401).body(errorResponse);
        }
        
        try {
            Product existingProduct = productDAO.getProductById(id);
            if (existingProduct == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Product not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            productDAO.deleteProduct(id);
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("message", "Product deleted successfully");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting product: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
