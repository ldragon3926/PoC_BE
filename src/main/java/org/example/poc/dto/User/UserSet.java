package org.example.poc.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Employee;
import org.example.poc.entity.Roles;
import org.example.poc.entity.Users;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Getter
@Setter
public class UserSet {
    private Integer id;
    private Integer employeeId;

    @NotBlank(message = "Khong duoc de email trong")
    private String email;

    @NotBlank(message = "Khong duoc de username trong")
    private String username;

    private String password;

    private String status;

    @NotEmpty(message = "Khong duoc de danh sach vai tro trong")
    private Set<Integer> idRoles;

    public Users dto(Users user, Employee employee, List<Roles> rolesFromDb) {
        user.setId(this.getId());
        user.setEmployee(employee);
        user.setEmail(this.getEmail());
        user.setUsername(this.getUsername());
        user.setPassword(this.getPassword());
        user.setStatus(isActiveStatus());
        if (rolesFromDb != null) {
            user.setRoles(new HashSet<>(rolesFromDb));
        }
        return user;
    }

    public boolean hasPassword() {
        return password != null && !password.trim().isEmpty();
    }

    public boolean isActiveStatus() {
        if (status == null) {
            return false;
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        return "ACTIVE".equals(normalized) || "TRUE".equals(normalized);
    }
}
