package org.example.poc.dto.Salary;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @NotNull(message = "Employee must not be empty")
    private Integer employeeId;

    @NotNull(message = "Month must not be empty")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year must not be empty")
    @Min(value = 2000, message = "Year is invalid")
    @Max(value = 3000, message = "Year is invalid")
    private Integer year;

    @NotNull(message = "Base salary must not be empty")
    private BigDecimal baseSalary;

    @NotNull(message = "Allowance must not be empty")
    private BigDecimal allowance;

    @NotNull(message = "Deduction must not be empty")
    private BigDecimal deduction;

    @NotNull(message = "Total salary must not be empty")
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
