// // import axios from 'axios';

// // const usersUrl = 'http://localhost:8080';


// // export const getUsers = async (id) => {
// //     id = id || '';
// //     try {
// //         return await axios.get(`${usersUrl}/users/${id}`);
// //     } catch (error) {
// //         console.log('Error while calling getUsers api ', error);
// //     }
// // }

// // export const addUser = async (user) => {
// //     return await axios.post(`${usersUrl}/user`, user);
// // }

// // export const deleteUser = async (id) => {
// //     return await axios.delete(`${usersUrl}/user/${id}`);
// // }

// // export const editUser = async (id, user) => {
// //     return await axios.put(`${usersUrl}/user`, user)
// // }


// import axios from 'axios';

// const usersUrl = 'http://localhost:8080';

// export const getUsers = async (id) => {
//   try {
//     const url = id ? `${usersUrl}/users/${id}` : `${usersUrl}/users`;
//     return await axios.get(url);
//   } catch (error) {
//     console.log('Error while calling getUsers api ', error);
//     throw error;
//   }
// };

// export const addUser = async (user) => {
//   return await axios.post(`${usersUrl}/user`, user);
// };

// export const deleteUser = async (id) => {
//   return await axios.delete(`${usersUrl}/user/${id}`);
// };

// export const editUser = async (user) => {
//   // ID is not needed in URL because backend expects only body
//   return await axios.put(`${usersUrl}/user`, user);
// };
import axios from 'axios';

const usersUrl = 'http://localhost:8080';

// ✅ Get all users or a single user by ID
export const getUsers = async (id = '') => {
  try {
    if (id) {
      return await axios.get(`${usersUrl}/users/${id}`);
    } else {
      return await axios.get(`${usersUrl}/users`);
    }
  } catch (error) {
    console.log('Error while calling getUsers API:', error);
  }
};

// ✅ Add a new user
export const addUser = async (user) => {
  try {
    return await axios.post(`${usersUrl}/user`, user, {
      headers: { 'Content-Type': 'application/json' },
    });
  } catch (error) {
    console.log('Error while calling addUser API:', error);
  }
};

// ✅ Delete a user by ID
export const deleteUser = async (id) => {
  try {
    return await axios.delete(`${usersUrl}/user/${id}`);
  } catch (error) {
    console.log('Error while calling deleteUser API:', error);
  }
};

// ✅ Edit/Update user details
export const editUser = async (id, user) => {
  try {
    return await axios.put(`${usersUrl}/user/${id}`, user, {
      headers: { 'Content-Type': 'application/json' },
    });
  } catch (error) {
    console.log('Error while calling editUser API:', error);
  }
};
