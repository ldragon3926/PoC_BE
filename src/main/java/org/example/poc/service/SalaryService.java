package org.example.poc.service;

import org.example.poc.entity.Salary;
import org.example.poc.entity.SalaryStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SalaryService {
    List<Salary> findAll();
    List<Salary> findAllFiltered(String keyword, Integer employeeId, Integer month, Integer year, SalaryStatus status);
    Optional<Salary> findById(Integer id);
    Salary getOne(Integer id);
    Salary add(Salary salary);
    Salary update(Salary salary, Integer id);
    int generateMonthFromAttendance(Integer month, Integer year, boolean overwriteDraft);
    int finalizeMonth(Integer month, Integer year);
    int payMonth(Integer month, Integer year);
    void delete(Integer id);
}
