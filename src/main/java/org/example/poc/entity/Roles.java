package org.example.poc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties({"permissions", "hibernateLazyInitializer", "handler"})
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;
    @Column(name = "name")
    private String name ;
    @Column(name = "code")
    private String code ;
    @Column(name = "description")
    private String description ;
    @Column(name = "status")
    private boolean status ;
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<Users> users = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "role_permissions",
    joinColumns = @JoinColumn(name = "role_id"),
    inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permissions> permissions = new HashSet<>();

    public List<Integer> getIdPermissions() {
        if (!Hibernate.isInitialized(permissions) || permissions == null) {
            return List.of();
        }
        return permissions.stream()
                .map(Permissions::getId)
                .toList();
    }

    public List<String> getPermissionNames() {
        if (!Hibernate.isInitialized(permissions) || permissions == null) {
            return List.of();
        }
        return permissions.stream()
                .map(permission -> permission.getName() != null ? permission.getName() : permission.getCode())
                .toList();
    }

}
