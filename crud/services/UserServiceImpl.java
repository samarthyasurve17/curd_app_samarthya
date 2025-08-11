package com.samarthyasApplication.crud.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samarthyasApplication.crud.dao.UserDao;
import com.samarthyasApplication.crud.model.User;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userdao;

	@Override
	public User addUser(User user) {
		userdao.save(user);
		return user;
		
	}

	@Override
	public List<User> getUsers() {
	return userdao.findAll();
	}

	@Override
	public User getUser(Long userId) {
	return userdao.findById(userId).get();
	}

	@Override
	public User updateUser(User user) {
		userdao.save(user);
		return user;
		
	}

	@Override
	public User deleteUser(Long userId) {
	User user =userdao.findById(userId).get();
	userdao.delete(user);
	return user;
	
	}
	
	
}
	