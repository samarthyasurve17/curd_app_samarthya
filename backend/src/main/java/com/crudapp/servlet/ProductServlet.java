package com.crudapp.servlet;

import com.crudapp.dao.ProductDAO;
import com.crudapp.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {
    private ProductDAO productDAO;
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || "/".equals(pathInfo)) {
                // Get all products
                List<Product> products = productDAO.getAllProducts();
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("success", true);
                responseMap.put("products", products);
                out.println(objectMapper.writeValueAsString(responseMap));
            } else if (pathInfo.startsWith("/")) {
                String idStr = pathInfo.substring(1);
                try {
                    Long id = Long.parseLong(idStr);
                    Product product = productDAO.getProductById(id);
                    if (product != null) {
                        Map<String, Object> responseMap = new HashMap<>();
                        responseMap.put("success", true);
                        responseMap.put("product", product);
                        out.println(objectMapper.writeValueAsString(responseMap));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("message", "Product not found");
                        out.println(objectMapper.writeValueAsString(errorResponse));
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Invalid product ID");
                    out.println(objectMapper.writeValueAsString(errorResponse));
                }
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter out = response.getWriter()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authentication required");
                out.println(objectMapper.writeValueAsString(errorResponse));
            }
            return;
        }
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if ("/".equals(pathInfo)) {
                handleCreateProduct(request, response, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Endpoint not found");
                out.println(objectMapper.writeValueAsString(errorResponse));
            }
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter out = response.getWriter()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authentication required");
                out.println(objectMapper.writeValueAsString(errorResponse));
            }
            return;
        }
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo != null && pathInfo.startsWith("/")) {
                String idStr = pathInfo.substring(1);
                try {
                    Long id = Long.parseLong(idStr);
                    handleUpdateProduct(request, response, out, id);
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Invalid product ID");
                    out.println(objectMapper.writeValueAsString(errorResponse));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Endpoint not found");
                out.println(objectMapper.writeValueAsString(errorResponse));
            }
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter out = response.getWriter()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authentication required");
                out.println(objectMapper.writeValueAsString(errorResponse));
            }
            return;
        }
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo != null && pathInfo.startsWith("/")) {
                String idStr = pathInfo.substring(1);
                try {
                    Long id = Long.parseLong(idStr);
                    handleDeleteProduct(response, out, id);
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Invalid product ID");
                    out.println(objectMapper.writeValueAsString(errorResponse));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Endpoint not found");
                out.println(objectMapper.writeValueAsString(errorResponse));
            }
        }
    }
    
    private void handleCreateProduct(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String quantityStr = request.getParameter("quantity");
        String category = request.getParameter("category");
        
        Map<String, Object> responseMap = new HashMap<>();
        
        if (name == null || name.trim().isEmpty() || priceStr == null || priceStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("success", false);
            responseMap.put("message", "Name and price are required");
        } else {
            try {
                BigDecimal price = new BigDecimal(priceStr.trim());
                Integer quantity = quantityStr != null && !quantityStr.trim().isEmpty() ? 
                    Integer.parseInt(quantityStr.trim()) : 0;
                
                Product product = new Product(
                    name.trim(),
                    description != null ? description.trim() : "",
                    price,
                    quantity,
                    category != null ? category.trim() : ""
                );
                
                Product savedProduct = productDAO.saveProduct(product);
                if (savedProduct != null) {
                    responseMap.put("success", true);
                    responseMap.put("message", "Product created successfully");
                    responseMap.put("product", savedProduct);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    responseMap.put("success", false);
                    responseMap.put("message", "Failed to create product");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid price or quantity format");
            }
        }
        
        out.println(objectMapper.writeValueAsString(responseMap));
    }
    
    private void handleUpdateProduct(HttpServletRequest request, HttpServletResponse response, PrintWriter out, Long id) 
            throws IOException {
        
        Product existingProduct = productDAO.getProductById(id);
        if (existingProduct == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Product not found");
            out.println(objectMapper.writeValueAsString(errorResponse));
            return;
        }
        
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String quantityStr = request.getParameter("quantity");
        String category = request.getParameter("category");
        
        Map<String, Object> responseMap = new HashMap<>();
        
        if (name != null && !name.trim().isEmpty()) {
            existingProduct.setName(name.trim());
        }
        if (description != null) {
            existingProduct.setDescription(description.trim());
        }
        if (priceStr != null && !priceStr.trim().isEmpty()) {
            try {
                BigDecimal price = new BigDecimal(priceStr.trim());
                existingProduct.setPrice(price);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid price format");
                out.println(objectMapper.writeValueAsString(responseMap));
                return;
            }
        }
        if (quantityStr != null && !quantityStr.trim().isEmpty()) {
            try {
                Integer quantity = Integer.parseInt(quantityStr.trim());
                existingProduct.setQuantity(quantity);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid quantity format");
                out.println(objectMapper.writeValueAsString(responseMap));
                return;
            }
        }
        if (category != null) {
            existingProduct.setCategory(category.trim());
        }
        
        if (productDAO.updateProduct(existingProduct)) {
            responseMap.put("success", true);
            responseMap.put("message", "Product updated successfully");
            responseMap.put("product", existingProduct);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("success", false);
            responseMap.put("message", "Failed to update product");
        }
        
        out.println(objectMapper.writeValueAsString(responseMap));
    }
    
    private void handleDeleteProduct(HttpServletResponse response, PrintWriter out, Long id) throws IOException {
        Map<String, Object> responseMap = new HashMap<>();
        
        if (productDAO.deleteProduct(id)) {
            responseMap.put("success", true);
            responseMap.put("message", "Product deleted successfully");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseMap.put("success", false);
            responseMap.put("message", "Product not found or failed to delete");
        }
        
        out.println(objectMapper.writeValueAsString(responseMap));
    }
}
