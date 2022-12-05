package com.docker.containers.appUser.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class AppUserDetails implements UserDetails {
    /**
     * YYYYMMVVV - year, month, version
     */
    private static final long serialVersionUID = 202211001L;
    private final Set<? extends GrantedAuthority> grantedAuthorities;
    private final Long appUserId;
    private final String username;;
    private final String password;
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean isCredentialsNonExpired;
    private final boolean isEnabled;

    public AppUserDetails(AppUser appUser) {
        this.appUserId = appUser.getAppUserId();
        this.username = appUser.getUsername();
        this.password = appUser.getPassword();
        this.grantedAuthorities = appUser.getUserRole().getGrantedAuthorities();
        this.isAccountNonExpired = appUser.isAccountNonExpired();
        this.isAccountNonLocked = appUser.isAccountNonLocked();
        this.isCredentialsNonExpired = appUser.isCredentialsNonExpired();
        this.isEnabled = appUser.isEnabled();
    }

    public AppUserDetails(Long appUserId, String username, AppUserRole appUserRole) {
        this.appUserId = appUserId;
        this.username = username;
        this.password = null;
        this.grantedAuthorities = appUserRole.getGrantedAuthorities();
        this.isEnabled = true;
        this.isCredentialsNonExpired = true;
        this.isAccountNonLocked = true;
        this.isAccountNonExpired = true;
    }

    public AppUserDetails(Set<? extends GrantedAuthority> grantedAuthorities, Long appUserId, String username,
                          String password, boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired,
                          boolean isEnabled) {
        this.grantedAuthorities = grantedAuthorities;
        this.appUserId = appUserId;
        this.username = username;
        this.password = password;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    public Long getAppUserId() {
        return appUserId;
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
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
