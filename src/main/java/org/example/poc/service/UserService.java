package org.example.poc.service;

import org.apache.catalina.User;
import org.example.poc.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
 List<Users> findAll();
 Users findByUsername(String username);
 Users add(Users users);
Users update (Users users, Integer id);
 void delete(Integer id);
}
