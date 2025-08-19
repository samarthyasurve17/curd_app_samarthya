import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

// Configure axios to talk to Spring Boot backend and send cookies
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.withCredentials = true;

const AuthContext = createContext();

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(false);

  const checkAuthStatus = async () => {
    try {
      // Check backend session first
      const response = await axios.get('/api/auth/check');
      if (response.data.success && response.data.authenticated) {
        setCurrentUser(response.data.user);
        setIsAuthenticated(true);
        // Store user in session storage
        sessionStorage.setItem('user', JSON.stringify(response.data.user));
      } else {
        // Check if user is logged in from session storage
        const user = sessionStorage.getItem('user');
        if (user) {
          setCurrentUser(JSON.parse(user));
          setIsAuthenticated(true);
        }
      }
    } catch (error) {
      console.error('Auth check error:', error);
      // Fallback to session storage
      const user = sessionStorage.getItem('user');
      if (user) {
        setCurrentUser(JSON.parse(user));
        setIsAuthenticated(true);
      }
    }
    setLoading(false);
  };

  // Expose a public method to refresh auth on-demand (e.g., after OTP verify)
  const refreshAuth = async () => {
    setLoading(true);
    await checkAuthStatus();
  };

  useEffect(() => {
    const initAuth = async () => {
      setLoading(true);
      
      // Check for OAuth success parameter
      const urlParams = new URLSearchParams(window.location.search);
      if (urlParams.get('login') === 'success') {
        // OAuth login successful, wait a moment then check auth
        await new Promise(resolve => setTimeout(resolve, 500));
        await checkAuthStatus();
        // Remove the query parameter from URL
        window.history.replaceState({}, document.title, window.location.pathname);
      } else {
        await checkAuthStatus();
      }
    };
    
    initAuth();
  }, []);

  const login = async (username, password) => {
    try {
      const formData = new FormData();
      formData.append('username', username);
      formData.append('password', password);

      const response = await axios.post('/api/auth/login', formData);
      
      if (response.data.success) {
        setCurrentUser(response.data.user);
        setIsAuthenticated(true);
        // Store user in session storage
        sessionStorage.setItem('user', JSON.stringify(response.data.user));
        return { success: true, message: response.data.message };
      } else {
        return { success: false, message: response.data.message };
      }
    } catch (error) {
      if (error.response) {
        return { success: false, message: error.response.data.message || 'Login failed' };
      }
      return { success: false, message: 'Network error occurred' };
    }
  };

  const register = async (userData) => {
    try {
      const formData = new FormData();
      formData.append('username', userData.username);
      formData.append('password', userData.password);
      formData.append('email', userData.email);
      formData.append('fullName', userData.fullName);

      const response = await axios.post('/api/auth/register', formData);
      
      if (response.data.success) {
        return { success: true, message: response.data.message };
      } else {
        return { success: false, message: response.data.message };
      }
    } catch (error) {
      if (error.response) {
        return { success: false, message: error.response.data.message || 'Registration failed' };
      }
      return { success: false, message: 'Network error occurred' };
    }
  };

  const googleLogin = () => {
    // Redirect to Spring Boot Google OAuth endpoint
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  const logout = async () => {
    try {
      await axios.post('/api/auth/logout');
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      setCurrentUser(null);
      setIsAuthenticated(false);
      sessionStorage.removeItem('user');
    }
  };

  const value = {
    currentUser,
    isAuthenticated,
    loading,
    refreshAuth,
    login,
    register,
    googleLogin,
    logout
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}
