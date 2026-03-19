package org.example.poc.service;

import org.example.poc.entity.Department;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface DepartmentService {
    List<Department> findAll();
    Optional<Department> findById(Integer id);
    Department getOne(Integer id);
    Department add(Department department);
    Department update(Department department, Integer id);
    void delete(Integer id);
}
