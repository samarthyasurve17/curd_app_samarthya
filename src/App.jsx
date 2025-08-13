import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from './routes/ProtectedRoute';

import NavBar from './components/NavBar';
import Login from './pages/Login';
import Register from './pages/Register';
import AllUsers from './components/AllUsers';
import AddUser from './components/AddUser';
import EditUser from './components/EditUser';

function LayoutWithNavbar({ children }) {
  return (
    <>
      <NavBar />
      <div style={{ padding: '16px' }}>{children}</div>
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public */}
        <Route
          path="/login"
          element={<Login />}
        />
        <Route
          path="/register"
          element={<Register />}
        />

        {/* Protected */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <LayoutWithNavbar>
                <AllUsers />
              </LayoutWithNavbar>
            </ProtectedRoute>
          }
        />
        <Route
          path="/add"
          element={
            <ProtectedRoute>
              <LayoutWithNavbar>
                <AddUser />
              </LayoutWithNavbar>
            </ProtectedRoute>
          }
        />
        <Route
          path="/edit/:id"
          element={
            <ProtectedRoute>
              <LayoutWithNavbar>
                <EditUser />
              </LayoutWithNavbar>
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
