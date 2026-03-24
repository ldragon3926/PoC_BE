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
        return userRepository.findAllWithRoles();
    }

    @Override
    public Optional<Users> findById(Integer id) {
        return userRepository.findByIdWithRoles(id);
    }

    @Override
    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Users add(Users users) {
        validateUniquenessOnCreate(users);
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return userRepository.save(users);
    }

    @Override
    @Transactional
    public Users update(Users users, Integer id) {
        Users foundUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Khong tim thay nguoi dung voi id: " + id));
        validateUniquenessOnUpdate(users, id);
        users.setId(foundUser.getId());
        if (users.getPassword() == null || users.getPassword().trim().isEmpty()) {
            users.setPassword(foundUser.getPassword());
        } else {
            users.setPassword(passwordEncoder.encode(users.getPassword()));
        }
        return userRepository.save(users);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public Users getOne(Integer id) {
        return userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new NotFoundExeption("Khong tim thay nguoi dung voi id: " + id));
    }

    private void validateUniquenessOnCreate(Users users) {
        if (users.getUsername() != null && userRepository.existsByUsernameIgnoreCase(users.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (users.getEmail() != null && userRepository.existsByEmailIgnoreCase(users.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        Integer employeeId = users.getEmployee() == null ? null : users.getEmployee().getId();
        if (employeeId != null && userRepository.existsByEmployee_Id(employeeId)) {
            throw new IllegalArgumentException("Employee already has an account");
        }
    }

    private void validateUniquenessOnUpdate(Users users, Integer id) {
        if (users.getUsername() != null && userRepository.existsUsernameConflictForUpdate(users.getUsername(), id)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (users.getEmail() != null && userRepository.existsEmailConflictForUpdate(users.getEmail(), id)) {
            throw new IllegalArgumentException("Email already exists");
        }
        Integer employeeId = users.getEmployee() == null ? null : users.getEmployee().getId();
        if (employeeId != null && userRepository.existsEmployeeConflictForUpdate(employeeId, id)) {
            throw new IllegalArgumentException("Employee already has an account");
        }
    }
}
