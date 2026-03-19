package org.example.poc.service;

import org.example.poc.entity.Salary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SalaryService {
    List<Salary> findAll();
    Salary add(Salary salary);
    Salary update(Salary salary, Integer id);
    void delete(Integer id);
}
