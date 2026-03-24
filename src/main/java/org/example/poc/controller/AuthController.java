package org.example.poc.controller;

import jakarta.validation.Valid;
import io.jsonwebtoken.JwtException;
import org.example.poc.config.CustomUserDetails;
import org.example.poc.dto.Auth.AuthProfileResponse;
import org.example.poc.config.Jwt.JwtUtil;
import org.example.poc.dto.Auth.AuthLoginRequest;
import org.example.poc.dto.Auth.AuthResponse;
import org.example.poc.entity.Roles;
import org.example.poc.entity.Users;
import org.example.poc.repository.UserRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.JwtTokenBlacklistService;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtTokenBlacklistService jwtTokenBlacklistService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          JwtTokenBlacklistService jwtTokenBlacklistService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.jwtTokenBlacklistService = jwtTokenBlacklistService;
        this.userRepository = userRepository;
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
        try {
            jwtTokenBlacklistService.blacklist(token, jwtUtil.getRemainingValiditySeconds(token));
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseUltils.error("AUTH_INVALID_TOKEN", "Token is invalid or expired");
        } catch (IllegalStateException ex) {
            return ResponseUltils.error("AUTH_BLACKLIST_UNAVAILABLE", "Redis blacklist is unavailable");
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

    @GetMapping("/me-profile")
    public ResponseEntity<?> meProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseUltils.error("AUTH_UNAUTHORIZED", "Unauthorized");
        }

        Optional<Users> userOpt = userRepository.findByIdWithRoles(userDetails.getId());
        if (userOpt.isEmpty()) {
            return ResponseUltils.error("error.user.not_found", "User does not exist");
        }

        Users user = userOpt.get();
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
        List<String> roleNames = user.getRoles() == null
                ? List.of()
                : user.getRoles().stream()
                .filter(role -> role != null && role.isStatus())
                .map(Roles::getCode)
                .toList();

        AuthProfileResponse payload = new AuthProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getEmployeeId(),
                user.getEmployeeName(),
                userDetails.getDepartmentId(),
                roleNames,
                authorities
        );

        return ResponseUltils.success(payload, "Get current user profile successfully", "AUTH_ME_PROFILE_SUCCESS");
    }
}
