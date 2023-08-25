package com.example.sample.server.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A trivial Spring Security UserDetails implementation.
 */
public final class UserProfile implements UserDetails {

    private static final long serialVersionUID = 1L;

    String username;
    String password;

    Set<String> authorities = new HashSet<>();

    public UserProfile(final String username, final String password, final Set<String> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

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
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
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