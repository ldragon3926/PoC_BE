package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Salary;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.SalaryRepository;
import org.example.poc.service.SalaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;

    @Override
    public List<Salary> findAll() {
        return salaryRepository.findAll();
    }

    @Override
    public Optional<Salary> findById(Integer id) {
        return salaryRepository.findById(id);
    }

    @Override
    public Salary getOne(Integer id) {
        return salaryRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Salary add(Salary salary) {
        return salaryRepository.save(salary);
    }

    @Override
    @Transactional
    public Salary update(Salary salary, Integer id) {
        Salary salaryFound = salaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find salary with id: " + id));
        salary.setId(salaryFound.getId());
        return salaryRepository.save(salary);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        salaryRepository.deleteById(id);
    }
}
