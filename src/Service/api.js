import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

// Attach token to every request
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Auto logout on 401/403
axios.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status;
    if (status === 401 || status === 403) {
      localStorage.removeItem('token');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(err);
  }
);

// Auth APIs
export const registerUser = (user) =>
  axios.post(`${BASE_URL}/auth/register`, user, {
    headers: { 'Content-Type': 'application/json' },
  });

export const loginUser = (creds) =>
  axios.post(`${BASE_URL}/auth/login`, creds, {
    headers: { 'Content-Type': 'application/json' },
  });

// CRUD APIs
export const getUsers = () => axios.get(`${BASE_URL}/users`);
export const getUser = (id) => axios.get(`${BASE_URL}/users/${id}`);
export const addUser = (user) =>
  axios.post(`${BASE_URL}/users`, user, {
    headers: { 'Content-Type': 'application/json' },
  });
export const editUser = (id, user) =>
  axios.put(`${BASE_URL}/users/${id}`, user, {
    headers: { 'Content-Type': 'application/json' },
  });
export const deleteUser = (id) => axios.delete(`${BASE_URL}/users/${id}`);
