package org.example.poc.service;

import org.example.poc.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeService {
    List<Employee> findAll();
    Employee add(Employee employee);
    Employee update(Employee employee, Integer id);
    void delete(Integer id);
}
