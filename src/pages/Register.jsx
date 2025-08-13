import React, { useState } from 'react';
import { registerUser } from '../Service/api';
import { useNavigate, Link } from 'react-router-dom';
import {
  Container,
  Box,
  TextField,
  Button,
  Typography,
  Paper,
} from '@mui/material';

export default function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleRegister = async () => {
    try {
      await registerUser({ username, password });
      alert('Registered successfully');
      navigate('/login');
    } catch {
      alert('Registration failed');
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
          Register
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
          onClick={handleRegister}
        >
          Register
        </Button>

        <Typography
          variant="body2"
          align="center"
          sx={{ mt: 2 }}
        >
          Already have an account?{' '}
          <Link
            to="/login"
            style={{ textDecoration: 'none', color: '#1976d2' }}
          >
            Login
          </Link>
        </Typography>
      </Paper>
    </Container>
  );
}
