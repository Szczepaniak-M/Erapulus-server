package com.erapulus.server.security;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;


public class JwtAuthenticatedUser extends AbstractAuthenticationToken {

    private final ApplicationUserEntity applicationUserEntity;

    public JwtAuthenticatedUser(ApplicationUserEntity applicationUserEntity, List<SimpleGrantedAuthority> authority) {
        super(authority);
        this.applicationUserEntity = applicationUserEntity;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return applicationUserEntity;
    }

    public Authentication asAuthentication() {
        return this;
    }
}
