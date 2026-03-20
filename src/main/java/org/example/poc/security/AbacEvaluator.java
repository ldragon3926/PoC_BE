package org.example.poc.security;

import jakarta.servlet.http.HttpServletRequest;
import org.example.poc.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AbacEvaluator {
    private static final Set<String> SELF_SERVICE_ACTIONS = Set.of(
            "GET:/api/v1/user/detail/",
            "PUT:/api/v1/user/update/"
    );

    public boolean isAllowed(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }

        if (authentication.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            return true;
        }

        String methodAndPath = request.getMethod() + ":" + request.getRequestURI();
        for (String prefix : SELF_SERVICE_ACTIONS) {
            if (methodAndPath.startsWith(prefix)) {
                Integer pathId = extractTrailingId(request.getRequestURI());
                return pathId != null && pathId.equals(userDetails.getId());
            }
        }

        return true;
    }

    private Integer extractTrailingId(String uri) {
        String[] segments = uri.split("/");
        if (segments.length == 0) {
            return null;
        }

        try {
            return Integer.valueOf(segments[segments.length - 1]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
