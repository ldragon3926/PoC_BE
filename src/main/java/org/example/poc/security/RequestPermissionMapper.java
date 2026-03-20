package org.example.poc.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class RequestPermissionMapper {

    public Optional<String> map(HttpServletRequest request) {
        String path = normalizePath(request.getRequestURI());
        String method = request.getMethod();

        if (!path.startsWith("/api/v1/")) {
            return Optional.empty();
        }

        String[] segments = path.substring("/api/v1/".length()).split("/");
        if (segments.length < 2) {
            return Optional.empty();
        }

        String resource = segments[0].replace('-', '_').toUpperCase(Locale.ROOT);
        String action = segments[1];

        if ("list-all".equals(action) && "GET".equalsIgnoreCase(method)) {
            return Optional.of("VIEW_" + resource + "_LIST");
        }
        if ("detail".equals(action) && "GET".equalsIgnoreCase(method)) {
            return Optional.of("VIEW_" + resource + "_DETAIL");
        }
        if ("create".equals(action) && "POST".equalsIgnoreCase(method)) {
            return Optional.of("VIEW_" + resource + "_CREATE");
        }
        if ("update".equals(action) && "PUT".equalsIgnoreCase(method)) {
            return Optional.of("VIEW_" + resource + "_UPDATE");
        }
        if ("delete".equals(action) && "DELETE".equalsIgnoreCase(method)) {
            return Optional.of("VIEW_" + resource + "_DELETE");
        }

        return Optional.empty();
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }
}
