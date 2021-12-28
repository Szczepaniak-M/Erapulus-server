package com.erapulus.server.security;

import java.util.List;

import com.erapulus.server.database.model.ApplicationUserEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


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

    public Authentication asAuthentication(){
        return this;
    }
}
