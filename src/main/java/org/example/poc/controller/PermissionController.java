package org.example.poc.controller;

import lombok.RequiredArgsConstructor;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(permissionService.findAll(), "Get permission list successfully", "VIEW_PERMISSION_LIST");
    }
}
