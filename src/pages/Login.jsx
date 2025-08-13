import React, { useState } from 'react';
import { loginUser } from '../Service/api';
import { useNavigate, Link } from 'react-router-dom';
import {
  Container,
  Box,
  TextField,
  Button,
  Typography,
  Paper,
} from '@mui/material';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const res = await loginUser({ username, password });
      localStorage.setItem('token', res.data.token);
      navigate('/');
    } catch {
      alert('Invalid credentials');
    }
  };

  return (
    <Container
      maxWidth="xs"
      sx={{ mt: 8 }}
    >
      <Paper
        elevation={4}
        sx={{ p: 4, borderRadius: 2 }}
      >
        <Typography
          variant="h5"
          align="center"
          gutterBottom
        >
          Login
        </Typography>

        <TextField
          label="Username"
          variant="outlined"
          fullWidth
          margin="normal"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <TextField
          label="Password"
          variant="outlined"
          type="password"
          fullWidth
          margin="normal"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <Button
          variant="contained"
          fullWidth
          sx={{ mt: 2, py: 1 }}
          onClick={handleLogin}
        >
          Login
        </Button>

        <Typography
          variant="body2"
          align="center"
          sx={{ mt: 2 }}
        >
          Don’t have an account?{' '}
          <Link
            to="/register"
            style={{ textDecoration: 'none', color: '#1976d2' }}
          >
            Register
          </Link>
        </Typography>
      </Paper>
    </Container>
  );
}
