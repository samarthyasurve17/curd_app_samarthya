package com.crudapp.repository;

import com.crudapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findAllByOrderByCreatedAtDesc();
    
    List<Product> findByCategoryOrderByCreatedAtDesc(String category);
    
    // Add missing methods for ProductService
    List<Product> findByCategory(String category);
    
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);
    
    @Query("SELECT DISTINCT category FROM Product WHERE category IS NOT NULL")
    List<String> findAllDistinctCategories();
    
    @Query("FROM Product WHERE name LIKE %:searchTerm% OR description LIKE %:searchTerm% OR category LIKE %:searchTerm% ORDER BY createdAt DESC")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT DISTINCT category FROM Product WHERE category IS NOT NULL")
    List<String> findAllCategories();
    
    List<Product> findByUserId(Long userId);
    
    @Query("SELECT DISTINCT p FROM Product p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Product> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}