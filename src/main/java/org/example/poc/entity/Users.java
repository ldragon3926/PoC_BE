package org.example.poc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_users_employee", columnNames = "employee_id")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties({"employee", "hibernateLazyInitializer", "handler"})
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;
    @Column(name = "email", unique = true)
    private String email ;
    @Column(name = "username", unique = true)
    private String username ;
    @JsonIgnore
    @Column(name = "password")
    private String password ;
    @Column(name = "status")
    private boolean status ;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true)
    private Employee employee;

    @ManyToMany
    @JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Roles> roles = new HashSet<>();

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
