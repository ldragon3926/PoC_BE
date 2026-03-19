package org.example.poc.service;

import org.example.poc.entity.Permissions;
import org.example.poc.entity.Roles;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PermissionService {
 List<Permissions> findAll();
 Permissions add(Permissions permissions);
 Permissions update (Permissions permissions, Integer id);
 void delete(Integer id);
}
