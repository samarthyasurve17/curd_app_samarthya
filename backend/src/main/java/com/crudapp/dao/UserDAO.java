package com.crudapp.dao;

import com.crudapp.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class UserDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public User saveUser(User user) {
        try {
            entityManager.persist(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public User getUserById(Long id) {
        try {
            return entityManager.find(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public User getUserByUsername(String username) {
        try {
            Query query = entityManager.createQuery("FROM User WHERE username = :username");
            query.setParameter("username", username);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public User getUserByEmail(String email) {
        try {
            Query query = entityManager.createQuery("FROM User WHERE email = :email");
            query.setParameter("email", email);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<User> getAllUsers() {
        try {
            Query query = entityManager.createQuery("FROM User");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public User updateUser(User user) {
        try {
            return entityManager.merge(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean deleteUser(Long id) {
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                entityManager.remove(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean authenticateUser(String username, String password) {
        try {
            Query query = entityManager.createQuery("FROM User WHERE username = :username AND password = :password");
            query.setParameter("username", username);
            query.setParameter("password", password);
            User user = (User) query.getSingleResult();
            return user != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isUsernameExists(String username) {
        try {
            Query query = entityManager.createQuery("SELECT COUNT(*) FROM User WHERE username = :username");
            query.setParameter("username", username);
            Long count = (Long) query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isEmailExists(String email) {
        try {
            Query query = entityManager.createQuery("SELECT COUNT(*) FROM User WHERE email = :email");
            query.setParameter("email", email);
            Long count = (Long) query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Add the missing createUser method that the controller expects
    public User createUser(User user) {
        return saveUser(user);
    }
}
