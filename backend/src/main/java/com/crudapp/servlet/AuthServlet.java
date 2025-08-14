package com.crudapp.servlet;

import com.crudapp.dao.UserDAO;
import com.crudapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO;
    private ObjectMapper objectMapper;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        objectMapper = new ObjectMapper();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if ("/login".equals(pathInfo)) {
                handleLogin(request, response, out);
            } else if ("/register".equals(pathInfo)) {
                handleRegister(request, response, out);
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(request, response, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Endpoint not found");
                out.println(objectMapper.writeValueAsString(errorResponse));
            }
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        Map<String, Object> responseMap = new HashMap<>();
        
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("success", false);
            responseMap.put("message", "Username and password are required");
        } else {
            if (userDAO.authenticateUser(username.trim(), password.trim())) {
                User user = userDAO.getUserByUsername(username.trim());
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                
                responseMap.put("success", true);
                responseMap.put("message", "Login successful");
                responseMap.put("user", user);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid username or password");
            }
        }
        
        out.println(objectMapper.writeValueAsString(responseMap));
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        
        Map<String, Object> responseMap = new HashMap<>();
        
        if (username == null || password == null || email == null || fullName == null ||
            username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty() || fullName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("success", false);
            responseMap.put("message", "All fields are required");
        } else {
            username = username.trim();
            email = email.trim();
            fullName = fullName.trim();
            
            if (userDAO.isUsernameExists(username)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                responseMap.put("success", false);
                responseMap.put("message", "Username already exists");
            } else if (userDAO.isEmailExists(email)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                responseMap.put("success", false);
                responseMap.put("message", "Email already exists");
            } else {
                User newUser = new User(username, password.trim(), email, fullName);
                User savedUser = userDAO.saveUser(newUser);
                
                if (savedUser != null) {
                    responseMap.put("success", true);
                    responseMap.put("message", "User registered successfully");
                    responseMap.put("user", savedUser);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    responseMap.put("success", false);
                    responseMap.put("message", "Failed to register user");
                }
            }
        }
        
        out.println(objectMapper.writeValueAsString(responseMap));
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        Map<String, Object> responseMap = new HashMap<>();
        
        if (session != null) {
            session.invalidate();
            responseMap.put("success", true);
            responseMap.put("message", "Logout successful");
        } else {
            responseMap.put("success", false);
            responseMap.put("message", "No active session");
        }
        
        out.println(objectMapper.writeValueAsString(responseMap));
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            if ("/check".equals(pathInfo)) {
                checkAuthStatus(request, response, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Endpoint not found");
                out.println(objectMapper.writeValueAsString(errorResponse));
            }
        }
    }
    
    private void checkAuthStatus(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        Map<String, Object> responseMap = new HashMap<>();
        
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            responseMap.put("success", true);
            responseMap.put("authenticated", true);
            responseMap.put("user", user);
        } else {
            responseMap.put("success", true);
            responseMap.put("authenticated", false);
        }
        
        out.println(objectMapper.writeValueAsString(responseMap));
    }
}
