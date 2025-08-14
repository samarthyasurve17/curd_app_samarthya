import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Check if user is logged in from session storage
    const user = sessionStorage.getItem('user');
    if (user) {
      setCurrentUser(JSON.parse(user));
      setIsAuthenticated(true);
    }
    setLoading(false);
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
    login,
    register,
    logout
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}
