package org.example.poc.service;

import org.example.poc.entity.Roles;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
 List<Roles> findAll();
 Roles add(Roles roles);
 Roles update (Roles roles, Integer id);
 void delete(Integer id);
}
