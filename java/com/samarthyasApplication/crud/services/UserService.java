package com.samarthyasApplication.crud.services;


import org.springframework.stereotype.Service;

import com.samarthyasApplication.crud.model.User;

@Service
public interface UserService {

	public User addUser(User user);
	public java.util.List<User> getUsers();
	public User getUser(Long userId);
	public User updateUser(User user);
	public User deleteUser(Long userId);
	
}
