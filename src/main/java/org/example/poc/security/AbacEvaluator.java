package org.example.poc.security;

import jakarta.servlet.http.HttpServletRequest;
import org.example.poc.config.CustomUserDetails;
import org.example.poc.repository.AttendanceRepository;
import org.example.poc.repository.ContractRepository;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.repository.RewardRepository;
import org.example.poc.repository.SalaryRepository;
import org.example.poc.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class AbacEvaluator {
    private static final String API_PREFIX = "/api/v1/";
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");
    private static final String ROLE_HR = "ROLE_HR";
    private static final String ROLE_MANAGER = "ROLE_MANAGER";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_KETOAN = "ROLE_KETOAN";
    private static final Set<String> SALARY_PRIVILEGED_ROLES = Set.of(ROLE_HR, ROLE_KETOAN);

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final AttendanceRepository attendanceRepository;
    private final ContractRepository contractRepository;
    private final RewardRepository rewardRepository;

    public AbacEvaluator(UserRepository userRepository,
                         EmployeeRepository employeeRepository,
                         SalaryRepository salaryRepository,
                         AttendanceRepository attendanceRepository,
                         ContractRepository contractRepository,
                         RewardRepository rewardRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.attendanceRepository = attendanceRepository;
        this.contractRepository = contractRepository;
        this.rewardRepository = rewardRepository;
    }

    public boolean isAllowed(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return false;
        }

        if (hasAnyRole(authentication, ADMIN_ROLES)) {
            return true;
        }

        String path = normalizePath(request.getRequestURI());
        if (!path.startsWith(API_PREFIX)) {
            return true;
        }

        String[] segments = path.substring(API_PREFIX.length()).split("/");
        if (segments.length < 2) {
            return true;
        }

        String resource = segments[0].toLowerCase(Locale.ROOT);
        String action = segments[1].toLowerCase(Locale.ROOT);

        if ("user".equals(resource) && ("detail".equals(action) || "update".equals(action))) {
            Integer userId = extractTrailingId(path);
            return userId != null && userId.equals(userDetails.getId());
        }

        if (!isProtectedResource(resource)) {
            return true;
        }

        return evaluateProtectedResource(authentication, userDetails, resource, action, path);
    }

    private boolean hasAnyRole(Authentication authentication, Set<String> requiredRoles) {
        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .anyMatch(requiredRoles::contains);
    }

    private boolean isProtectedResource(String resource) {
        return Set.of("employee", "salary", "attendance", "contract", "reward").contains(resource);
    }

    private boolean evaluateProtectedResource(Authentication authentication,
                                              CustomUserDetails userDetails,
                                              String resource,
                                              String action,
                                              String path) {
        if ("salary".equals(resource)) {
            if (hasAnyRole(authentication, SALARY_PRIVILEGED_ROLES)) {
                return true;
            }
            return evaluateOwnerBoundAction(resource, action, path, userDetails.getEmployeeId());
        }

        if ("attendance".equals(resource)) {
            if (hasAnyRole(authentication, Set.of(ROLE_HR))) {
                return true;
            }
            return evaluateAttendanceForUserAction(resource, action, path, userDetails.getEmployeeId());
        }

        if (hasAnyRole(authentication, Set.of(ROLE_HR))) {
            return true;
        }

        if (hasAnyRole(authentication, Set.of(ROLE_MANAGER))) {
            return evaluateManagerDepartmentBoundAction(resource, action, path, userDetails.getDepartmentId());
        }

        return evaluateOwnerBoundAction(resource, action, path, userDetails.getEmployeeId());
    }

    private boolean evaluateAttendanceForUserAction(String resource,
                                                    String action,
                                                    String path,
                                                    Integer userEmployeeId) {
        if ("list-all".equals(action) || "create".equals(action)) {
            return userEmployeeId != null;
        }
        return evaluateOwnerBoundAction(resource, action, path, userEmployeeId);
    }

    private boolean evaluateOwnerBoundAction(String resource,
                                             String action,
                                             String path,
                                             Integer userEmployeeId) {
        if ("list-all".equals(action) || "create".equals(action)) {
            return false;
        }

        if (!("detail".equals(action) || "update".equals(action) || "delete".equals(action))) {
            return true;
        }

        Integer resourceId = extractTrailingId(path);
        if (resourceId == null || userEmployeeId == null) {
            return false;
        }

        Integer ownerEmployeeId = resolveOwnerEmployeeId(resource, resourceId);
        return ownerEmployeeId != null && ownerEmployeeId.equals(userEmployeeId);
    }

    private boolean evaluateManagerDepartmentBoundAction(String resource,
                                                         String action,
                                                         String path,
                                                         Integer userDepartmentId) {
        if ("list-all".equals(action) || "create".equals(action)) {
            return false;
        }

        if (!("detail".equals(action) || "update".equals(action) || "delete".equals(action))) {
            return true;
        }

        Integer resourceId = extractTrailingId(path);
        if (resourceId == null || userDepartmentId == null) {
            return false;
        }

        Integer ownerDepartmentId = resolveOwnerDepartmentId(resource, resourceId);
        return ownerDepartmentId != null && ownerDepartmentId.equals(userDepartmentId);
    }

    private Integer resolveOwnerEmployeeId(String resource, Integer id) {
        return switch (resource) {
            case "employee" -> employeeRepository.findById(id).map(employee -> employee.getId()).orElse(null);
            case "salary" -> salaryRepository.findByIdWithEmployee(id).map(salary -> salary.getEmployeeId()).orElse(null);
            case "attendance" -> attendanceRepository.findByIdWithEmployee(id).map(attendance -> attendance.getEmployeeId()).orElse(null);
            case "contract" -> contractRepository.findByIdWithEmployee(id).map(contract -> contract.getEmployeeId()).orElse(null);
            case "reward" -> rewardRepository.findByIdWithEmployee(id).map(reward -> reward.getEmployeeId()).orElse(null);
            case "user" -> userRepository.findByIdWithRoles(id).map(user -> user.getEmployeeId()).orElse(null);
            default -> null;
        };
    }

    private Integer resolveOwnerDepartmentId(String resource, Integer id) {
        return switch (resource) {
            case "employee" -> employeeRepository.findByIdWithDepartment(id)
                    .map(employee -> employee.getDepartmentId())
                    .orElse(null);
            case "attendance" -> attendanceRepository.findByIdWithEmployee(id)
                    .map(attendance -> attendance.getEmployee() == null ? null : attendance.getEmployee().getDepartmentId())
                    .orElse(null);
            case "contract" -> contractRepository.findByIdWithEmployee(id)
                    .map(contract -> contract.getEmployee() == null ? null : contract.getEmployee().getDepartmentId())
                    .orElse(null);
            case "reward" -> rewardRepository.findByIdWithEmployee(id)
                    .map(reward -> reward.getEmployee() == null ? null : reward.getEmployee().getDepartmentId())
                    .orElse(null);
            default -> null;
        };
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    private Integer extractTrailingId(String path) {
        String[] segments = path.split("/");
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
