package org.example.poc.dto.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Permissions;
import org.example.poc.entity.Roles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RoleSet {
    private Integer id;
    @NotBlank(message = "Khong duoc de ten vai tro trong")
    private String name;
    @NotBlank(message = "Khong duoc de ma vai tro trong")
    private String code;
    @NotBlank(message = "Khong duoc de mo ta vai tro trong")
    private String description;
    private boolean status;
    @NotEmpty(message = "Khong duoc de danh sach quyen trong")
    private Set<Integer> idPermissions;

    public Roles dto(Roles role, List<Permissions> permissions) {
        role.setId(this.getId());
        role.setName(this.getName());
        role.setCode(this.getCode());
        role.setDescription(this.getDescription());
        role.setStatus(this.isStatus());
        if (permissions != null) {
            role.setPermissions(new HashSet<>(permissions));
        }
        return role;
    }
}
