package org.example.poc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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

}
