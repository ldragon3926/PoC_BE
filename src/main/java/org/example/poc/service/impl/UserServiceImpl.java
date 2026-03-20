package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import org.example.poc.entity.Users;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.UserRepository;
import org.example.poc.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<Users> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Users add(Users users) {
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return userRepository.save(users);
    }

    @Override
    @Transactional
    public Users update(Users users, Integer id) {
        Users foundUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Khong tim thay nguoi dung voi id: " + id));
        users.setId(foundUser.getId());
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return userRepository.save(users);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public Users getOne(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundExeption("Khong tim thay nguoi dung voi id: " + id));
    }
}
