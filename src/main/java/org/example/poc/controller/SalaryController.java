package org.example.poc.controller;

import lombok.RequiredArgsConstructor;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/salary")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryService salaryService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(salaryService.findAll(), "Get salary list successfully", "VIEW_SALARY_LIST");
    }
}
