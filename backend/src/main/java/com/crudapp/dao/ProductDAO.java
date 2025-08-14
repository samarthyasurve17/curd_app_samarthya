package com.crudapp.dao;

import com.crudapp.model.Product;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class ProductDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Product saveProduct(Product product) {
        try {
            entityManager.persist(product);
            return product;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Product getProductById(Long id) {
        try {
            return entityManager.find(Product.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Product> getAllProducts() {
        try {
            Query query = entityManager.createQuery("FROM Product ORDER BY createdAt DESC");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Product> getProductsByCategory(String category) {
        try {
            Query query = entityManager.createQuery("FROM Product WHERE category = :category ORDER BY createdAt DESC");
            query.setParameter("category", category);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Product> searchProducts(String searchTerm) {
        try {
            Query query = entityManager.createQuery(
                "FROM Product WHERE name LIKE :searchTerm OR description LIKE :searchTerm OR category LIKE :searchTerm ORDER BY createdAt DESC");
            query.setParameter("searchTerm", "%" + searchTerm + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Product updateProduct(Product product) {
        try {
            return entityManager.merge(product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean deleteProduct(Long id) {
        try {
            Product product = entityManager.find(Product.class, id);
            if (product != null) {
                entityManager.remove(product);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Add the missing createProduct method that the controller expects
    public Product createProduct(Product product) {
        return saveProduct(product);
    }
}
