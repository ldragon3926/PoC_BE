package org.example.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.poc.config.CustomUserDetails;
import org.example.poc.dto.Attendance.AttendanceSet;
import org.example.poc.entity.Attendance;
import org.example.poc.entity.Employee;
import org.example.poc.repository.EmployeeRepository;
import org.example.poc.response.ResponseUltils;
import org.example.poc.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/list-all")
    public ResponseEntity<?> listAll(Authentication authentication) {
        if (isSelfAttendanceScope(authentication)) {
            Integer employeeId = extractEmployeeId(authentication);
            if (employeeId == null) {
                return ResponseUltils.error("error.attendance.employee_not_found", "Employee profile does not exist");
            }
            return ResponseUltils.success(attendanceService.findByEmployeeId(employeeId), "Get attendance list successfully", "VIEW_ATTENDANCE_LIST");
        }
        return ResponseUltils.success(attendanceService.findAll(), "Get attendance list successfully", "VIEW_ATTENDANCE_LIST");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        Optional<Attendance> attendance = attendanceService.findById(id);
        if (attendance.isEmpty()) {
            return ResponseUltils.error("error.attendance.not_found", "Attendance does not exist");
        }
        return ResponseUltils.success(attendanceService.getOne(id), "Get attendance detail successfully", "VIEW_ATTENDANCE_DETAIL");
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(Authentication authentication,
                                 @RequestBody @Valid AttendanceSet attendanceSet,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.attendance.validation", error);
        }
        if (isSelfAttendanceScope(authentication)) {
            Integer employeeId = extractEmployeeId(authentication);
            if (employeeId == null) {
                return ResponseUltils.error("error.attendance.employee_not_found", "Employee profile does not exist");
            }
            attendanceSet.setEmployeeId(employeeId);
        }
        Employee employee = employeeRepository.findById(attendanceSet.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseUltils.error("error.attendance.employee_not_found", "Employee does not exist");
        }
        Attendance attendance = attendanceSet.dto(new Attendance(), employee);
        return ResponseUltils.success(attendanceService.add(attendance), "Create attendance successfully", "VIEW_ATTENDANCE_CREATE");
    }

    private boolean isSelfAttendanceScope(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        boolean hasAttendanceManagerScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_HR".equals(authority.getAuthority()));
        return !hasAttendanceManagerScope;
    }

    private Integer extractEmployeeId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return null;
        }
        return userDetails.getEmployeeId();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid AttendanceSet attendanceSet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return ResponseUltils.error("error.attendance.validation", error);
        }
        Employee employee = employeeRepository.findById(attendanceSet.getEmployeeId()).orElse(null);
        if (employee == null) {
            return ResponseUltils.error("error.attendance.employee_not_found", "Employee does not exist");
        }
        Attendance attendance = attendanceSet.dto(new Attendance(), employee);
        return ResponseUltils.success(attendanceService.update(attendance, id), "Update attendance successfully", "VIEW_ATTENDANCE_UPDATE");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (attendanceService.findById(id).isEmpty()) {
            return ResponseUltils.error("error.attendance.not_found", "Attendance does not exist");
        }
        attendanceService.delete(id);
        return ResponseUltils.success(null, "Delete attendance successfully", "VIEW_ATTENDANCE_DELETE");
    }
}
