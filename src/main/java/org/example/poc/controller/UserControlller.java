package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.User.UserSet;
import org.example.poc.entity.Employee;
import org.example.poc.entity.Roles;
import org.example.poc.entity.Users;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.repository.RoleRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserControlller {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(userService.findAll(), "Lay tat ca danh sach thanh cong", "VIEW_USER_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Users> users = userService.findById(id);
        if (users.isEmpty()) {
            return ResponseUltils.error("error.user.not_found", "Nguoi dung khong ton tai");
        }
        return ResponseUltils.success(userService.getOne(id), "Lay thong tin chi tiet nguoi dung thanh cong", "VIEW_USER_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid UserSet userSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.user.validation", error);
        }
        if (!userSet.hasPassword()) {
            return ResponseUltils.error("error.user.validation", "Khong duoc de password trong");
        }
        List<Roles> roles = roleRepository.findAllById(userSet.getIdRoles());
        Employee employee = userSet.getEmployeeId() == null ? null : employeeRepository.findById(userSet.getEmployeeId()).orElse(null);
        if (userSet.getEmployeeId() != null && employee == null) {
            return ResponseUltils.error("error.user.employee_not_found", "Employee does not exist");
        }
        Users users = userSet.dto(new Users(), employee, roles);
        return ResponseUltils.success(userService.add(users), "Them moi user thanh cong", "VIEW_USER_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid UserSet userSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.user.validation", error);
        }
        List<Roles> roles = roleRepository.findAllById(userSet.getIdRoles());
        Employee employee = userSet.getEmployeeId() == null ? null : employeeRepository.findById(userSet.getEmployeeId()).orElse(null);
        if (userSet.getEmployeeId() != null && employee == null) {
            return ResponseUltils.error("error.user.employee_not_found", "Employee does not exist");
        }
        Users users = userSet.dto(new Users(), employee, roles);
        return ResponseUltils.success(userService.update(users, id), "Update user theo id thanh cong", "VIEW_USER_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.user.not_found", "Nguoi dung khong ton tai");
        }
        userService.delete(id);
        return ResponseUltils.success(null, "Delete user theo id thanh cong", "VIEW_USER_DELETE");
    }
}
