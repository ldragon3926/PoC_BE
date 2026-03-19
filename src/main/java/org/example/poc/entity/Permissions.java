package org.example.poc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "permissions")
public class Permissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name ;
    @Column(name = "code")
    private String code ;
    @Column(name = "description")
    private String description ;
    @Column(name = "status")
    private boolean status ;
    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private Set<Roles> roles = new HashSet<>();

}
