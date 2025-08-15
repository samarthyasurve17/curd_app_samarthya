package com.crudapp.repository;

import com.crudapp.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void shouldSaveAndFindProduct() {
        // given
        Product product = new Product("Test Product", "Test Description", new BigDecimal("99.99"), 10, "Test Category");
        entityManager.persist(product);
        entityManager.flush();

        // when
        Product found = productRepository.findById(product.getId()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test Product");
        assertThat(found.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
    }

    @Test
    public void shouldFindProductsByCategory() {
        // given
        Product product1 = new Product("Product 1", "Description 1", new BigDecimal("19.99"), 5, "Category A");
        Product product2 = new Product("Product 2", "Description 2", new BigDecimal("29.99"), 10, "Category A");
        Product product3 = new Product("Product 3", "Description 3", new BigDecimal("39.99"), 15, "Category B");
        
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // when
        List<Product> categoryAProducts = productRepository.findByCategory("Category A");
        List<Product> categoryBProducts = productRepository.findByCategory("Category B");

        // then
        assertThat(categoryAProducts).hasSize(2);
        assertThat(categoryBProducts).hasSize(1);
        assertThat(categoryAProducts).extracting(Product::getName).containsExactlyInAnyOrder("Product 1", "Product 2");
        assertThat(categoryBProducts).extracting(Product::getName).containsExactly("Product 3");
    }

    @Test
    public void shouldFindProductsByNameOrDescription() {
        // given
        Product product1 = new Product("Laptop", "High performance laptop", new BigDecimal("999.99"), 5, "Electronics");
        Product product2 = new Product("Phone", "Smartphone with great camera", new BigDecimal("499.99"), 10, "Electronics");
        Product product3 = new Product("Camera", "Professional DSLR", new BigDecimal("799.99"), 3, "Photography");
        
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();

        // when
        List<Product> laptopResults = productRepository.findByNameContainingOrDescriptionContaining("Laptop", "Laptop");
        List<Product> cameraResults = productRepository.findByNameContainingOrDescriptionContaining("camera", "camera");

        // then
        assertThat(laptopResults).hasSize(1);
        assertThat(cameraResults).hasSize(2); // Should find both the Camera product and the Phone with "camera" in description
        assertThat(laptopResults).extracting(Product::getName).containsExactly("Laptop");
        assertThat(cameraResults).extracting(Product::getName).containsExactlyInAnyOrder("Phone", "Camera");
    }

    @Test
    public void shouldFindAllDistinctCategories() {
        // given
        Product product1 = new Product("Product 1", "Description 1", new BigDecimal("19.99"), 5, "Category A");
        Product product2 = new Product("Product 2", "Description 2", new BigDecimal("29.99"), 10, "Category A");
        Product product3 = new Product("Product 3", "Description 3", new BigDecimal("39.99"), 15, "Category B");
        Product product4 = new Product("Product 4", "Description 4", new BigDecimal("49.99"), 20, "Category C");
        
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.persist(product4);
        entityManager.flush();

        // when
        List<String> categories = productRepository.findAllDistinctCategories();

        // then
        assertThat(categories).hasSize(3);
        assertThat(categories).containsExactlyInAnyOrder("Category A", "Category B", "Category C");
    }
}