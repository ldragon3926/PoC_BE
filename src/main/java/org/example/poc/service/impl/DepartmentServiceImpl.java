package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Department;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.DepartmentRepository;
import org.example.poc.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public Optional<Department> findById(Integer id) {
        return departmentRepository.findById(id);
    }

    @Override
    public Department getOne(Integer id) {
        return departmentRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Department add(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public Department update(Department department, Integer id) {
        Department departmentFound = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find department with id: " + id));
        department.setId(departmentFound.getId());
        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        departmentRepository.deleteById(id);
    }
}
