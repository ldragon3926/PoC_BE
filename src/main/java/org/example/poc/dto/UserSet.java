package org.example.poc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.Roles;
import org.example.poc.entity.Users;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserSet {
    private Integer id;
    @NotBlank(message = "Không được để gmail trống")
    private String gmail;
    @NotBlank(message = "Không được để username trống")
    private String username;
    @NotBlank(message = "Không được để password trống")
    private String password;
    private boolean status;

    private Set<Long> idRoles;

    public Users dto(Users user, List<Roles> idRolesfromDB) {
        user.setId(this.getId());
        user.setGmail(this.getUsername());
        user.setUsername(this.getUsername());
        user.setPassword(this.getPassword());
        user.setStatus(isStatus());
        if(idRolesfromDB != null ){
        user.setRoles(new HashSet<>(idRolesfromDB));
        }
        return user;
    }
}
