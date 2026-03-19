package org.example.poc.dto.Department;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Department;

@Getter
@Setter
public class DepartmentSet {
    private Integer id;
    @NotBlank(message = "Không được để tên phòng ban trống")
    private String name;
    @NotBlank(message = "Không được để mô tả phòng ban trống")
    private String description;

    public Department dto(Department department) {
        department.setId(this.getId());
        department.setName(this.getName());
        department.setDescription(this.getDescription());
        return department;
    }
}
