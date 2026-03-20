package org.example.poc.dto.Contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Contract;
import org.example.poc.entity.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractSet {
    private Integer id;

    @NotNull(message = "Khong duoc de nhan vien trong")
    private Integer employeeId;

    @NotBlank(message = "Khong duoc de loai hop dong trong")
    private String contractType;

    @NotNull(message = "Khong duoc de luong co ban trong")
    private BigDecimal baseSalary;

    @NotNull(message = "Khong duoc de he so luong trong")
    private BigDecimal salaryCoefficient;

    @NotNull(message = "Khong duoc de ngay bat dau trong")
    private LocalDate startDate;

    @NotNull(message = "Khong duoc de ngay ket thuc trong")
    private LocalDate endDate;

    public Contract dto(Contract contract, Employee employee) {
        contract.setId(this.getId());
        contract.setEmployee(employee);
        contract.setContractType(this.getContractType());
        contract.setBaseSalary(this.getBaseSalary());
        contract.setSalaryCoefficient(this.getSalaryCoefficient());
        contract.setStartDate(this.getStartDate());
        contract.setEndDate(this.getEndDate());
        return contract;
    }
}
