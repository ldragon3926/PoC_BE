package org.example.poc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "rewards")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Lob
    @Column(name = "reason")
    private String reason;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Integer getEmployeeId() {
        if (employee == null || !Hibernate.isInitialized(employee)) {
            return null;
        }
        return employee.getId();
    }

    public String getEmployeeName() {
        if (employee == null || !Hibernate.isInitialized(employee)) {
            return null;
        }
        return employee.getName();
    }

}
