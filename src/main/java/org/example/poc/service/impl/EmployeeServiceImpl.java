package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Employee;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAllWithDepartment();
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        return employeeRepository.findByIdWithDepartment(id);
    }

    @Override
    public Employee getOne(Integer id) {
        return employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find employee with id: " + id));
    }

    @Override
    @Transactional
    public Employee add(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee update(Employee employee, Integer id) {
        Employee employeeFound = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find employee with id: " + id));
        employee.setId(employeeFound.getId());
        employee.setCreatedAt(employeeFound.getCreatedAt());
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        employeeRepository.deleteById(id);
    }
}
