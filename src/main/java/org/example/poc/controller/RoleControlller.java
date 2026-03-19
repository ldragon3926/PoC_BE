package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.Role.RoleSet;
import org.example.poc.entity.Permissions;
import org.example.poc.entity.Roles;
import org.example.poc.repository.PermissionRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleControlller {
    private final RoleService roleService;
    private final PermissionRepository permissionRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(roleService.findAll(), "Get role list successfully", "VIEW_ROLE_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Roles> role = roleService.findById(id);
        if (role.isEmpty()) {
            return ResponseUltils.error("error.role.not_found", "Role does not exist");
        }
        return ResponseUltils.success(roleService.getOne(id), "Get role detail successfully", "VIEW_ROLE_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid RoleSet roleSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.role.validation", error);
        }
        List<Permissions> permissions = permissionRepository.findAllById(roleSet.getIdPermissions());
        Roles role = roleSet.dto(new Roles(), permissions);
        return ResponseUltils.success(roleService.add(role), "Create role successfully", "VIEW_ROLE_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid RoleSet roleSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.role.validation", error);
        }
        List<Permissions> permissions = permissionRepository.findAllById(roleSet.getIdPermissions());
        Roles role = roleSet.dto(new Roles(), permissions);
        return ResponseUltils.success(roleService.update(role, id), "Update role successfully", "VIEW_ROLE_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (roleService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.role.not_found", "Role does not exist");
        }
        roleService.delete(id);
        return ResponseUltils.success(null, "Delete role successfully", "VIEW_ROLE_DELETE");
    }
}
