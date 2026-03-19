package org.example.poc.service;

import org.example.poc.entity.Roles;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RoleService {
    List<Roles> findAll();
    Optional<Roles> findById(Integer id);
    Roles getOne(Integer id);
    Roles add(Roles roles);
    Roles update(Roles roles, Integer id);
    void delete(Integer id);
}
