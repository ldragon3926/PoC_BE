package org.example.poc.service;

import org.example.poc.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
 List<Users> findAll();
 Users findByUsername(String username);
 Users add(Users users);
Users update (Users users, Integer id);
 void delete(Integer id);
 Optional<Users> findById(Integer id);
 Users getOne(Integer id);
}
