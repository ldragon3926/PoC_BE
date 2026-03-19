package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.Department.DepartmentSet;
import org.example.poc.entity.Department;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(departmentService.findAll(), "Get department list successfully", "VIEW_DEPARTMENT_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Department> department = departmentService.findById(id);
        if (department.isEmpty()) {
            return ResponseUltils.error("error.department.not_found", "Department does not exist");
        }
        return ResponseUltils.success(departmentService.getOne(id), "Get department detail successfully", "VIEW_DEPARTMENT_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid DepartmentSet departmentSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.department.validation", error);
        }
        Department department = departmentSet.dto(new Department());
        return ResponseUltils.success(departmentService.add(department), "Create department successfully", "VIEW_DEPARTMENT_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid DepartmentSet departmentSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.department.validation", error);
        }
        Department department = departmentSet.dto(new Department());
        return ResponseUltils.success(departmentService.update(department, id), "Update department successfully", "VIEW_DEPARTMENT_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (departmentService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.department.not_found", "Department does not exist");
        }
        departmentService.delete(id);
        return ResponseUltils.success(null, "Delete department successfully", "VIEW_DEPARTMENT_DELETE");
    }
}
