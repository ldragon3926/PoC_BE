package org.example.poc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Department;
import org.example.poc.entity.Employee;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeSet {
    private Integer id;
    @NotBlank(message = "Không được để tên nhân viên trống")
    private String name;
    @NotBlank(message = "Không được để email trống")
    private String email;
    @NotBlank(message = "Không được để số điện thoại trống")
    private String phone;
    @NotBlank(message = "Không được để địa chỉ trống")
    private String address;
    @NotNull(message = "Không được để ngày sinh trống")
    private LocalDate dob;
    @NotNull(message = "Không được để phòng ban trống")
    private Integer departmentId;
    private Instant createdAt;

    public Employee dto(Employee employee, Department department) {
        employee.setId(this.getId());
        employee.setName(this.getName());
        employee.setEmail(this.getEmail());
        employee.setPhone(this.getPhone());
        employee.setAddress(this.getAddress());
        employee.setDob(this.getDob());
        employee.setDepartment(department);
        employee.setCreatedAt(this.getCreatedAt());
        return employee;
    }
}
