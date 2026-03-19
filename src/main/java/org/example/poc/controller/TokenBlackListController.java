package org.example.poc.controller;

import lombok.RequiredArgsConstructor;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.TokenBlackListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token-black-list")
@RequiredArgsConstructor
public class TokenBlackListController {
    private final TokenBlackListService tokenBlackListService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(tokenBlackListService.findAll(), "Get token blacklist successfully", "VIEW_TOKEN_BLACKLIST_LIST");
    }
}
