package org.example.poc.config;

import org.example.poc.entity.Permissions;
import org.example.poc.entity.Roles;
import org.example.poc.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {
    private final Integer id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Integer id, String username, String password, boolean enabled,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static CustomUserDetails fromUser(Users user) {
        Set<SimpleGrantedAuthority> grantedAuthorities = new LinkedHashSet<>();

        if (user.getRoles() != null) {
            for (Roles role : user.getRoles()) {
                if (role == null || !role.isStatus()) {
                    continue;
                }

                if (role.getCode() != null && !role.getCode().isBlank()) {
                    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode().trim()));
                }

                if (role.getPermissions() != null) {
                    for (Permissions permission : role.getPermissions()) {
                        if (permission != null && permission.isStatus()
                                && permission.getCode() != null && !permission.getCode().isBlank()) {
                            grantedAuthorities.add(new SimpleGrantedAuthority(permission.getCode().trim()));
                        }
                    }
                }
            }
        }

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isStatus(),
                grantedAuthorities
        );
    }

    public Integer getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
