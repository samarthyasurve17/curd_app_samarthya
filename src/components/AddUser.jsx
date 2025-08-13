import React, { useState } from 'react';
import {
  FormGroup,
  FormControl,
  InputLabel,
  Input,
  Button,
  styled,
  Typography,
} from '@mui/material';
import { addUser } from '../Service/api';
import { useNavigate } from 'react-router-dom';

const initialValue = { name: '', username: '', email: '', phone: '' };
const Container = styled(FormGroup)`
  width: 50%;
  margin: 5% 0 0 25%;
  & > div {
    margin-top: 20px;
  }
`;

export default function AddUser() {
  const [user, setUser] = useState(initialValue);
  const { name, username, email, phone } = user;
  const navigate = useNavigate();

  const onValueChange = (e) =>
    setUser({ ...user, [e.target.name]: e.target.value });

  const addUserDetails = async () => {
    await addUser(user);
    navigate('/'); // go back to all users
  };

  return (
    <Container>
      <Typography variant="h4">Add User</Typography>
      <FormControl>
        <InputLabel>Name</InputLabel>
        <Input
          name="name"
          value={name}
          onChange={onValueChange}
        />
      </FormControl>
      <FormControl>
        <InputLabel>Username</InputLabel>
        <Input
          name="username"
          value={username}
          onChange={onValueChange}
        />
      </FormControl>
      <FormControl>
        <InputLabel>Email</InputLabel>
        <Input
          name="email"
          value={email}
          onChange={onValueChange}
        />
      </FormControl>
      <FormControl>
        <InputLabel>Phone</InputLabel>
        <Input
          name="phone"
          value={phone}
          onChange={onValueChange}
        />
      </FormControl>
      <FormControl>
        <Button
          variant="contained"
          color="primary"
          onClick={addUserDetails}
        >
          Add User
        </Button>
      </FormControl>
    </Container>
  );
}
