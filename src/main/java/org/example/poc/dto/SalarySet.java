package org.example.poc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Employee;
import org.example.poc.entity.Salary;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class SalarySet {
    private Integer id;
    @NotNull(message = "Không được để nhân viên trống")
    private Integer employeeId;
    @NotNull(message = "Không được để tháng trống")
    private Integer month;
    @NotNull(message = "Không được để năm trống")
    private Integer year;
    @NotNull(message = "Không được để lương cơ bản trống")
    private BigDecimal baseSalary;
    @NotNull(message = "Không được để phụ cấp trống")
    private BigDecimal allowance;
    @NotNull(message = "Không được để khoản khấu trừ trống")
    private BigDecimal deduction;
    @NotNull(message = "Không được để tổng lương trống")
    private BigDecimal totalSalary;
    private Instant createdAt;

    public Salary dto(Salary salary, Employee employee) {
        salary.setId(this.getId());
        salary.setEmployee(employee);
        salary.setMonth(this.getMonth());
        salary.setYear(this.getYear());
        salary.setBaseSalary(this.getBaseSalary());
        salary.setAllowance(this.getAllowance());
        salary.setDeduction(this.getDeduction());
        salary.setTotalSalary(this.getTotalSalary());
        salary.setCreatedAt(this.getCreatedAt());
        return salary;
    }
}
