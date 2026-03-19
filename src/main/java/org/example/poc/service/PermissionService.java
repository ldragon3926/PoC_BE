package org.example.poc.service;

import org.example.poc.entity.Permissions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PermissionService {
    List<Permissions> findAll();
    Optional<Permissions> findById(Integer id);
    Permissions getOne(Integer id);
    Permissions add(Permissions permissions);
    Permissions update(Permissions permissions, Integer id);
    void delete(Integer id);
}
