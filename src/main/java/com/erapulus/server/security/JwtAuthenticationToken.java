package com.erapulus.server.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String jwt;

    public JwtAuthenticationToken(String jwt) {
        super(Collections.emptyList());
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return jwt;
    }
}