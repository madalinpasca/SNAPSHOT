package com.madalin.wisetraveller.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class WiseTravellerUserDetails implements UserDetails,CredentialsContainer {
    private Collection<? extends GrantedAuthority> authorities;
    private String password;
    private String username;
    private Long id;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    public boolean isApproved() {
        return isAccountNonExpired() &&
                isAccountNonLocked() &&
                isCredentialsNonExpired() &&
                isEnabled();
    }
}
