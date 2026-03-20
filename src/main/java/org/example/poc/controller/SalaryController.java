package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.Salary.SalarySet;
import org.example.poc.entity.Employee;
import org.example.poc.entity.Salary;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/salary")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryService salaryService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(salaryService.findAll(), "Get salary list successfully", "VIEW_SALARY_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Salary> salary = salaryService.findById(id);
        if (salary.isEmpty()) {
            return ResponseUltils.error("error.salary.not_found", "Salary does not exist");
        }
        return ResponseUltils.success(salaryService.getOne(id), "Get salary detail successfully", "VIEW_SALARY_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid SalarySet salarySet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.salary.validation", error);
        }
        Employee employee = employeeRepository.findById(salarySet.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseUltils.error("error.salary.employee_not_found", "Employee does not exist");
        }
        Salary salary = salarySet.dto(new Salary(), employee);
        return ResponseUltils.success(salaryService.add(salary), "Create salary successfully", "VIEW_SALARY_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid SalarySet salarySet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.salary.validation", error);
        }
        Employee employee = employeeRepository.findById(salarySet.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseUltils.error("error.salary.employee_not_found", "Employee does not exist");
        }
        Salary salary = salarySet.dto(new Salary(), employee);
        return ResponseUltils.success(salaryService.update(salary, id), "Update salary successfully", "VIEW_SALARY_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (salaryService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.salary.not_found", "Salary does not exist");
        }
        salaryService.delete(id);
        return ResponseUltils.success(null, "Delete salary successfully", "VIEW_SALARY_DELETE");
    }
}
