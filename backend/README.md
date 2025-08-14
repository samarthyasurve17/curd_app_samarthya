# CRUD Application Backend (Spring Boot)

A Spring Boot-based backend with JPA, MySQL database, and RESTful API endpoints.

## 🚀 Quick Setup in STS

### 1. Import Project
- Open STS
- File → Import → Maven → Existing Maven Projects
- Browse to this `backend` folder
- Click Finish

### 2. Configure Database
- Open `src/main/resources/application.properties`
- Update the database password: `your_password_here` → your actual MySQL password
- Make sure MySQL is running on port 3306

### 3. Run the Application
- **Right-click on your project**
- **Select `Run As`** → **`Java Application`**
- **Choose `CrudApplication`** from the list
- **Click `OK`**

**That's it! No Tomcat needed!** 🎉

## 🌐 Access Points
- **Backend**: http://localhost:8080
- **API Endpoints**: http://localhost:8080/api/*
- **Frontend**: http://localhost:3000

## 📊 Database Setup
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Run the SQL script from `database/setup.sql`
4. Update password in `application.properties`

## 🔧 Technology Stack
- **Java 11**
- **Spring Boot 2.7.14**
- **Spring Data JPA**
- **Spring Security**
- **MySQL 8.0**
- **Maven**

## 📁 Project Structure
```
src/main/
├── java/com/crudapp/
│   ├── model/          # Entity classes (User, Product)
│   ├── dao/            # Data Access Objects
│   ├── controller/     # REST API controllers
│   ├── config/         # Spring configuration
│   └── CrudApplication.java  # Main Spring Boot class
├── resources/
│   └── application.properties
└── webapp/
    └── index.html
```

## 🚨 Troubleshooting
- **Port 8080 in use**: Change port in `application.properties` (server.port=8081)
- **Database connection failed**: Check MySQL service and password
- **Class not found**: Run Maven → Update Project
- **404 errors**: Check controller mappings

## 📝 API Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout
- `GET /api/products` - Get all products
- `POST /api/products` - Create product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

## 🔑 Default Users
- **Admin**: `admin` / `admin123`
- **User**: `user1` / `user123`
- **User**: `user2` / `user123`

## 🎯 Benefits of Spring Boot
✅ **No server setup needed** - embedded Tomcat  
✅ **Auto-configuration** - Spring Boot handles most setup  
✅ **Easy to run** - just run the main class  
✅ **Auto-reload** - changes reflect immediately  
✅ **Better error handling** - detailed error messages  
✅ **Production ready** - easy to deploy  

## 🚀 Alternative Ways to Run

### Method 1: Java Application (Recommended)
- Right-click → Run As → Java Application

### Method 2: Maven Command
- Right-click → Run As → Maven build...
- Goals: `spring-boot:run`

### Method 3: Command Line
```bash
mvn spring-boot:run
```

## 🔄 What Changed from Servlet Version
- **Servlets** → **Spring Controllers** (same functionality)
- **web.xml** → **application.properties**
- **Manual CORS** → **Spring CORS configuration**
- **Hibernate config** → **Spring Data JPA**
- **Tomcat deployment** → **Embedded server**
