package com.samarthyasApplication.crud.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samarthyasApplication.crud.model.User;

public interface UserDao extends JpaRepository<User, Long>{

}
