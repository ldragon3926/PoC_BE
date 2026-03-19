package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.dto.TokenBlackList.TokenBlackListSet;
import org.example.poc.entity.TokenBlackList;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.TokenBlackListService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/token-black-list")
@RequiredArgsConstructor
public class TokenBlackListController {
    private final TokenBlackListService tokenBlackListService;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll() {
        return ResponseUltils.success(tokenBlackListService.findAll(), "Get token blacklist successfully", "VIEW_TOKEN_BLACKLIST_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<TokenBlackList> tokenBlackList = tokenBlackListService.findById(id);
        if (tokenBlackList.isEmpty()) {
            return ResponseUltils.error("error.token_blacklist.not_found", "Token blacklist does not exist");
        }
        return ResponseUltils.success(tokenBlackListService.getOne(id), "Get token blacklist detail successfully", "VIEW_TOKEN_BLACKLIST_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody @Valid TokenBlackListSet tokenBlackListSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.token_blacklist.validation", error);
        }
        TokenBlackList tokenBlackList = tokenBlackListSet.dto(new TokenBlackList());
        return ResponseUltils.success(tokenBlackListService.add(tokenBlackList), "Create token blacklist successfully", "VIEW_TOKEN_BLACKLIST_CREATE");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid TokenBlackListSet tokenBlackListSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.token_blacklist.validation", error);
        }
        TokenBlackList tokenBlackList = tokenBlackListSet.dto(new TokenBlackList());
        return ResponseUltils.success(tokenBlackListService.update(tokenBlackList, id), "Update token blacklist successfully", "VIEW_TOKEN_BLACKLIST_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (tokenBlackListService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.token_blacklist.not_found", "Token blacklist does not exist");
        }
        tokenBlackListService.delete(id);
        return ResponseUltils.success(null, "Delete token blacklist successfully", "VIEW_TOKEN_BLACKLIST_DELETE");
    }
}
