package com.madalin.wisetraveller.model.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    User,
    Owner,
    Admin;

    private static final String rolePrefix = "ROLE_";

    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(rolePrefix  + toRoleName());
    }

    public String toRoleName() {
        return toString().toUpperCase();
    }
}
