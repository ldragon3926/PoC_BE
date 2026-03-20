package org.example.poc.controller;

import jakarta.validation.Valid;
import org.example.poc.config.CustomUserDetails;
import org.example.poc.config.Jwt.JwtUtil;
import org.example.poc.dto.Auth.AuthLoginRequest;
import org.example.poc.dto.Auth.AuthResponse;
import org.example.poc.entity.TokenBlackList;
import org.example.poc.repository.TokenBlackListRepository;
import org.example.poc.response.ResponseUltils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlackListRepository tokenBlackListRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          TokenBlackListRepository tokenBlackListRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlackListRepository = tokenBlackListRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateTokenJWT(userDetails);
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return ResponseUltils.success(
                new AuthResponse(token, userDetails.getId(), userDetails.getUsername(), authorities),
                "Login successfully",
                "AUTH_LOGIN_SUCCESS"
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseUltils.error("AUTH_INVALID_TOKEN", "Authorization header is missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        if (!tokenBlackListRepository.existsByToken(token)) {
            TokenBlackList tokenBlackList = new TokenBlackList();
            tokenBlackList.setToken(token);
            tokenBlackList.setExpiryDate(jwtUtil.extractExpiration(token).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            tokenBlackListRepository.save(tokenBlackList);
        }

        return ResponseUltils.success(null, "Logout successfully", "AUTH_LOGOUT_SUCCESS");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseUltils.error("AUTH_UNAUTHORIZED", "Unauthorized");
        }

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return ResponseUltils.success(
                new AuthResponse(null, userDetails.getId(), userDetails.getUsername(), authorities),
                "Get current user successfully",
                "AUTH_ME_SUCCESS"
        );
    }
}
