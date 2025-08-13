import React, { useEffect, useState } from 'react';
import {
  FormGroup,
  FormControl,
  InputLabel,
  Input,
  Button,
  styled,
  Typography,
} from '@mui/material';
import { getUser, editUser } from '../Service/api';
import { useNavigate, useParams } from 'react-router-dom';

const initialValue = { name: '', username: '', email: '', phone: '' };
const Container = styled(FormGroup)`
  width: 50%;
  margin: 5% 0 0 25%;
  & > div {
    margin-top: 20px;
  }
`;

export default function EditUser() {
  const [user, setUser] = useState(initialValue);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const loadUser = async () => {
      const res = await getUser(id);
      setUser(res.data);
    };
    loadUser();
  }, [id]);

  const onValueChange = (e) =>
    setUser({ ...user, [e.target.name]: e.target.value });

  const updateUser = async () => {
    await editUser(id, user);
    navigate('/');
  };

  const { name, username, email, phone } = user;

  return (
    <Container>
      <Typography variant="h4">Edit User</Typography>
      <FormControl>
        <InputLabel>Name</InputLabel>
        <Input
          name="name"
          value={name || ''}
          onChange={onValueChange}
        />
      </FormControl>
      <FormControl>
        <InputLabel>Username</InputLabel>
        <Input
          name="username"
          value={username || ''}
          onChange={onValueChange}
        />
      </FormControl>
      <FormControl>
        <InputLabel>Email</InputLabel>
        <Input
          name="email"
          value={email || ''}
          onChange={onValueChange}
        />
      </FormControl>
      <FormControl>
        <InputLabel>Phone</InputLabel>
        <Input
          name="phone"
          value={phone || ''}
          onChange={onValueChange}
        />
      </FormControl>
      <FormControl>
        <Button
          variant="contained"
          color="primary"
          onClick={updateUser}
        >
          Update User
        </Button>
      </FormControl>
    </Container>
  );
}
