package org.example.poc.service;

import org.example.poc.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EmployeeService {
    List<Employee> findAll();
    Optional<Employee> findById(Integer id);
    Employee getOne(Integer id);
    Employee add(Employee employee);
    Employee update(Employee employee, Integer id);
    void delete(Integer id);
}
