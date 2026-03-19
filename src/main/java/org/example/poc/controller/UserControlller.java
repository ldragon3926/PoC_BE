package org.example.poc.controller;

import jakarta.validation.Valid;
import org.example.poc.dto.User.UserSet;
import org.example.poc.entity.Roles;
import org.example.poc.entity.Users;
import org.example.poc.repository.RoleRepository;
import org.example.poc.repository.UserRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserControlller {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(userService.findAll(), "Lấy tất cả danh sách thành công", "VIEW_USER_LIST");
    }

    //@GetMapping("/profile")
//    public ResponseEntity<?> detail(){
//    String username  = SecurityContextHolder.getContext().getAuthentication().getName();
//    return ResponseUltils.success(userService.findByUsername(username), "Lấy thông tin người dùng hiện tại thành công","VIEW_USER_DETAIL");
//}
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable(name = "id") Integer id) {
        Optional<Users> users = userService.findById(id);
        if (users.isEmpty()) {
            return ResponseUltils.error("error.user.not_found", "Người dùng không tồn tại");
        }
        return ResponseUltils.success(userService.getOne(id), "Lấy thông tin chi tiết người dùng thành công", "VIEW_USER_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid UserSet userSet, BindingResult bindingResult) {
        Users newUsers = new Users();
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.user.validation", error);
        }
        List<Roles> roles = roleRepository.findAllById(userSet.getIdRoles());
        Users users = userSet.dto(newUsers, roles);
        return ResponseUltils.success(userService.add(users), "Thêm mới user thành công", "VIEW_USER_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Integer id, @RequestBody @Valid UserSet userSet, BindingResult bindingResult) {
        Users newUsers = new Users();
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.user.validation", error);
        }
        List<Roles> roles = roleRepository.findAllById(userSet.getIdRoles());
        Users users = userSet.dto(newUsers, roles);
        return ResponseUltils.success(userService.update(users, id), "Update user theo Id: " + id + " thành công", "VIEW_USER_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Integer id) {
        Optional<Users> users = userService.findById(id);
        if (users.isEmpty()) {
            return ResponseUltils.error("", "");
        }
        userService.delete(id);
        return ResponseUltils.success(null, "Delete user theo Id: " + id +" thành công","VIEW_USER_DELETE");
    }
}
