package pl.put.erasmusbackend.security;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.put.erasmusbackend.database.model.ApplicationUserEntity;


public class JwtAuthenticatedUser extends AbstractAuthenticationToken {

    private final ApplicationUserEntity applicationUserEntity;

    public JwtAuthenticatedUser(ApplicationUserEntity applicationUserEntity, SimpleGrantedAuthority authority) {
        super(Collections.singletonList(authority));
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
}
