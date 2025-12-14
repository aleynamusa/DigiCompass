package com.digicompass.backend.configuration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public List<String> getRoles() {
        if (authorities == null) return List.of();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a != null && !a.isEmpty())
                .collect(Collectors.toList());
    }

    public boolean hasRole(String role) {
        if (role == null || role.isEmpty()) return false;
        return getRoles().stream().anyMatch(r -> r.equalsIgnoreCase(role) || r.equalsIgnoreCase("ROLE_" + role.replace("ROLE_", "")));
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
