import React from 'react';
import { AppBar, Toolbar, styled } from '@mui/material';
import { NavLink, useNavigate } from 'react-router-dom';

const Header = styled(AppBar)`
  background: #111111;
`;
const Tabs = styled(NavLink)`
  color: #ffffff;
  margin-right: 20px;
  text-decoration: none;
  font-size: 20px;
  &.active {
    font-weight: bold;
    border-bottom: 2px solid white;
  }
`;

export default function NavBar() {
  const navigate = useNavigate();
  const logout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <Header position="static">
      <Toolbar>
        <Tabs
          to="/"
          end
        >
          CRUD Application
        </Tabs>
        <Tabs to="/">All Users</Tabs>
        <Tabs to="/add">Add User</Tabs>
        <button
          onClick={logout}
          style={{
            marginLeft: 'auto',
            background: 'transparent',
            color: '#fff',
            border: '1px solid #fff',
            padding: '6px 10px',
            cursor: 'pointer',
          }}
        >
          Logout
        </button>
      </Toolbar>
    </Header>
  );
}
