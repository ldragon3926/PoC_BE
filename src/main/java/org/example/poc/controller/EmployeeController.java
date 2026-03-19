package org.example.poc.controller;

import lombok.RequiredArgsConstructor;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(employeeService.findAll(), "Get employee list successfully", "VIEW_EMPLOYEE_LIST");
    }
}
