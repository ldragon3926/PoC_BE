package org.example.poc.controller;

import org.example.poc.repository.UserRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserControlller {
@Autowired
UserService userService;
@Autowired
    UserRepository userRepository;
@GetMapping("/list-all")
    public ResponseEntity<?> listAll(){
    return ResponseUltils.success(userService.findAll(), "Lấy tất cả danh sách thành công", "VIEW_USER_LIST");
}
//@GetMapping("/profile")
//    public ResponseEntity<?> detail(){
//    String username  = SecurityContextHolder.getContext().getAuthentication().getName();
//    return ResponseUltils.success(userService.findByUsername(username), "Lấy thông tin người dùng hiện tại thành công","VIEW_USER_DETAIL");
//}


}
