# CRUD Application - Spring Boot Migration

This project is a migration of the original servlet-based CRUD application to Spring Boot. The application provides a RESTful API for managing products with user authentication.

## Features

- User authentication (login, registration, logout)
- Product management (CRUD operations)
- Spring Security integration
- Spring Data JPA for database operations
- RESTful API endpoints

## Project Structure

```
src/main/java/com/crudapp/
├── CrudApplication.java              # Main Spring Boot application class
├── config/
│   └── SecurityConfig.java           # Spring Security configuration
├── controller/
│   ├── AuthController.java           # Authentication endpoints
│   └── ProductController.java        # Product management endpoints
├── model/
│   ├── Product.java                  # Product entity
│   └── User.java                     # User entity with Spring Security integration
├── repository/
│   ├── ProductRepository.java        # Spring Data JPA repository for products
│   └── UserRepository.java           # Spring Data JPA repository for users
└── service/
    ├── ProductService.java           # Business logic for product operations
    └── UserService.java              # Business logic for user operations and UserDetailsService implementation
```

## API Endpoints

### Authentication

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout
- `GET /api/auth/check` - Check authentication status

### Products

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create a new product (requires authentication)
- `PUT /api/products/{id}` - Update a product (requires authentication)
- `DELETE /api/products/{id}` - Delete a product (requires authentication)

## Setup and Running

### Prerequisites

- Java 8 or higher
- Maven
- MySQL database

### Configuration

The application is configured to connect to a MySQL database. Update the database connection settings in `src/main/resources/application.properties` if needed.

### Building and Running

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/crudapp-0.0.1-SNAPSHOT.jar
```

Alternatively, you can run the application using Maven:

```bash
mvn spring-boot:run
```

The application will be available at http://localhost:8080

## Testing

The project includes unit tests for repositories and integration tests for controllers. Run the tests using:

```bash
mvn test
```

## Migration Notes

### Changes from Original Application

- Replaced servlet-based controllers with Spring MVC controllers
- Replaced Hibernate DAO classes with Spring Data JPA repositories
- Integrated Spring Security for authentication
- Added proper dependency injection
- Improved error handling and response formatting
- Added comprehensive test coverage