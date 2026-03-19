package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.Permission.PermissionSet;
import org.example.poc.entity.Permissions;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(permissionService.findAll(), "Get permission list successfully", "VIEW_PERMISSION_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Permissions> permission = permissionService.findById(id);
        if (permission.isEmpty()) {
            return ResponseUltils.error("error.permission.not_found", "Permission does not exist");
        }
        return ResponseUltils.success(permissionService.getOne(id), "Get permission detail successfully", "VIEW_PERMISSION_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid PermissionSet permissionSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.permission.validation", error);
        }
        Permissions permission = permissionSet.dto(new Permissions());
        return ResponseUltils.success(permissionService.add(permission), "Create permission successfully", "VIEW_PERMISSION_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid PermissionSet permissionSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.permission.validation", error);
        }
        Permissions permission = permissionSet.dto(new Permissions());
        return ResponseUltils.success(permissionService.update(permission, id), "Update permission successfully", "VIEW_PERMISSION_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (permissionService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.permission.not_found", "Permission does not exist");
        }
        permissionService.delete(id);
        return ResponseUltils.success(null, "Delete permission successfully", "VIEW_PERMISSION_DELETE");
    }
}
