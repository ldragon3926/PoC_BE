package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import org.example.poc.entity.Users;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.UserRepository;
import org.example.poc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

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
        return userRepository.save(users);
    }

    @Override
    @Transactional
    public Users update(Users users, Integer id) {
        Users uFind = userRepository.findById(id).orElseThrow(() -> new NotFoundExeption("Không tìm thấy người dùng với id: " + id));
        users.setId(uFind.getId());
        return userRepository.save(users);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public Users getOne(Integer id) {
        return userRepository.findById(id).get();
    }
}
