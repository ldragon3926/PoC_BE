package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.Employee.EmployeeSet;
import org.example.poc.entity.Department;
import org.example.poc.entity.Employee;
import org.example.poc.repository.DepartmentRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(employeeService.findAll(), "Get employee list successfully", "VIEW_EMPLOYEE_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Employee> employee = employeeService.findById(id);
        if (employee.isEmpty()) {
            return ResponseUltils.error("error.employee.not_found", "Employee does not exist");
        }
        return ResponseUltils.success(employeeService.getOne(id), "Get employee detail successfully", "VIEW_EMPLOYEE_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid EmployeeSet employeeSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.employee.validation", error);
        }
        Department department = departmentRepository.findById(employeeSet.getDepartmentId()).orElse(null);
        Employee employee = employeeSet.dto(new Employee(), department);
        return ResponseUltils.success(employeeService.add(employee), "Create employee successfully", "VIEW_EMPLOYEE_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid EmployeeSet employeeSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.employee.validation", error);
        }
        Department department = departmentRepository.findById(employeeSet.getDepartmentId()).orElse(null);
        Employee employee = employeeSet.dto(new Employee(), department);
        return ResponseUltils.success(employeeService.update(employee, id), "Update employee successfully", "VIEW_EMPLOYEE_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (employeeService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.employee.not_found", "Employee does not exist");
        }
        employeeService.delete(id);
        return ResponseUltils.success(null, "Delete employee successfully", "VIEW_EMPLOYEE_DELETE");
    }
}
