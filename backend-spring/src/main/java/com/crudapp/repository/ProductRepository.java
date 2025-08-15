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
    
    @Query("FROM Product WHERE name LIKE %:searchTerm% OR description LIKE %:searchTerm% OR category LIKE %:searchTerm% ORDER BY createdAt DESC")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT DISTINCT category FROM Product WHERE category IS NOT NULL")
    List<String> findAllCategories();
}