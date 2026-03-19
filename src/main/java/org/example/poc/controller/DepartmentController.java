package org.example.poc.controller;

import lombok.RequiredArgsConstructor;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(departmentService.findAll(), "Get department list successfully", "VIEW_DEPARTMENT_LIST");
    }
}
