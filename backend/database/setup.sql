-- Create the database
CREATE DATABASE IF NOT EXISTS crudapp;
USE crudapp;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 0,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', 'admin123', 'admin@example.com', 'Administrator', 'ADMIN'),
('user1', 'user123', 'user1@example.com', 'John Doe', 'USER'),
('user2', 'user123', 'user2@example.com', 'Jane Smith', 'USER');

INSERT INTO products (name, description, price, quantity, category) VALUES
('Laptop', 'High-performance laptop with latest specifications', 999.99, 10, 'Electronics'),
('Smartphone', 'Latest smartphone with advanced features', 699.99, 15, 'Electronics'),
('Coffee Maker', 'Automatic coffee maker for home use', 89.99, 25, 'Home & Kitchen'),
('Running Shoes', 'Comfortable running shoes for athletes', 129.99, 30, 'Sports'),
('Book: Programming Guide', 'Comprehensive guide to programming', 49.99, 50, 'Books');

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_created_at ON products(created_at);
