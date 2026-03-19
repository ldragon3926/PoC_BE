package org.example.poc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Permissions;

@Getter
@Setter
public class PermissionSet {
    private Integer id;
    @NotBlank(message = "Không được để tên quyền trống")
    private String name;
    @NotBlank(message = "Không được để mã quyền trống")
    private String code;
    @NotBlank(message = "Không được để mô tả quyền trống")
    private String description;
    private boolean status;

    public Permissions dto(Permissions permissions) {
        permissions.setId(this.getId());
        permissions.setName(this.getName());
        permissions.setCode(this.getCode());
        permissions.setDescription(this.getDescription());
        permissions.setStatus(this.isStatus());
        return permissions;
    }
}
