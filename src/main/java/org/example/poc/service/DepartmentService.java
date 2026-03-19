package org.example.poc.service;

import org.example.poc.entity.Department;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {
    List<Department> findAll();
    Department add(Department department);
    Department update(Department department, Integer id);
    void delete(Integer id);
}
