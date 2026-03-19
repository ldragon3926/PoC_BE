package org.example.poc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Permissions;
import org.example.poc.entity.Roles;

import java.util.HashSet;
import java.util.List;

@Getter
@Setter
public class RoleSet {
    private Integer id;
    @NotBlank(message = "Không được để tên vai trò trống")
    private String name;
    @NotBlank(message = "Không được để mã vai trò trống")
    private String code;
    @NotBlank(message = "Không được để mô tả vai trò trống")
    private String description;
    private boolean status;

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
