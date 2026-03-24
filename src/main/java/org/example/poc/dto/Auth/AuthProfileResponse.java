package org.example.poc.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AuthProfileResponse {
    private Integer userId;
    private String username;
    private String email;
    private Integer employeeId;
    private String employeeName;
    private Integer departmentId;
    private List<String> roleNames;
    private List<String> authorities;
}
