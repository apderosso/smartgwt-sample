package com.example.sample.server.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * A trivial Spring Security UserDetails implementation.
 */
@Data
@RequiredArgsConstructor
public final class UserProfile implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final String      username;
    private final String      password;
    private final Set<String> authorities;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> granted = new HashSet<>();

        for (final String authority : authorities) {
            granted.add(new SimpleGrantedAuthority(authority));
        }
        return granted;
    }

}