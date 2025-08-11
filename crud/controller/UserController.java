package com.samarthyasApplication.crud.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.samarthyasApplication.crud.model.User;
import com.samarthyasApplication.crud.services.UserService;

@RestController
@CrossOrigin
public class UserController {
	
	@Autowired
	private UserService userservice;
	
@PostMapping("/user")
	public User addUser(@RequestBody User user) {
	return this.userservice.addUser(user);
		
	}
@GetMapping("/users")
public List<User> getUsers(){
	return this.userservice.getUsers();

}

@GetMapping("/users/{userId}")
public User getUser(@PathVariable String userId) {
	return this.userservice.getUser(Long.parseLong(userId));
	
}

@PutMapping("/user/{userId}")
public User updateUser(@PathVariable Long userId, @RequestBody User user) {
    user.setId(userId);  // Ensures correct ID is updated
    return this.userservice.updateUser(user);
}
@DeleteMapping("/user/{userId}")
public User deleteUser(@PathVariable String userId) {
return this.userservice.deleteUser(Long.parseLong(userId));
}
}
