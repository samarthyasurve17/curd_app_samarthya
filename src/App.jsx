import AllUsers from './Components/AllUsers';
import AddUser from './Components/AddUser';
import EditUser from './Components/EditUser';
import NavBar from './Components/NavBar';
import Curdapp from './Components/Curdapp';

import { BrowserRouter, Route, Routes } from 'react-router-dom';

function App() {
  return (
    <BrowserRouter>
      <NavBar />
      <Routes>
        <Route
          path="/"
          element={<Curdapp />}
        />
        <Route
          path="/all"
          element={<AllUsers />}
        />
        <Route
          path="/add"
          element={<AddUser />}
        />
        <Route
          path="/edit/:id"
          element={<EditUser />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
