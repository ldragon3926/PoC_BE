package org.example.poc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Contract;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractSet {
    private Integer id;
    @NotBlank(message = "Không được để loại hợp đồng trống")
    private String contractType;
    @NotNull(message = "Không được để lương cơ bản trống")
    private BigDecimal baseSalary;
    @NotNull(message = "Không được để hệ số lương trống")
    private BigDecimal salaryCoefficient;
    @NotNull(message = "Không được để ngày bắt đầu trống")
    private LocalDate startDate;
    @NotNull(message = "Không được để ngày kết thúc trống")
    private LocalDate endDate;

    public Contract dto(Contract contract) {
        contract.setId(this.getId());
        contract.setContractType(this.getContractType());
        contract.setBaseSalary(this.getBaseSalary());
        contract.setSalaryCoefficient(this.getSalaryCoefficient());
        contract.setStartDate(this.getStartDate());
        contract.setEndDate(this.getEndDate());
        return contract;
    }
}
