package org.example.poc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "contract_type", length = 50)
    private String contractType;
    @Column(name = "base_salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;
    @Column(name = "salary_coefficient", precision = 5, scale = 2)
    private BigDecimal salaryCoefficient;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;

}