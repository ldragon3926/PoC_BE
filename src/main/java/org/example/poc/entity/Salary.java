package org.example.poc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "salaries")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    @Column(name = "month")
    private Integer month;
    @Column(name = "year")
    private Integer year;
    @Column(name = "base_salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;
    @Column(name = "allowance", precision = 15, scale = 2)
    private BigDecimal allowance;
    @Column(name = "deduction", precision = 15, scale = 2)
    private BigDecimal deduction;
    @Column(name = "total_salary", precision = 15, scale = 2)
    private BigDecimal totalSalary;
    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at")
    private Instant createdAt;

}