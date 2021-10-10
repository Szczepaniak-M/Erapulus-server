package pl.put.erasmusbackend.security;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.put.erasmusbackend.database.model.ApplicationUser;


public class JwtAuthenticatedUser extends AbstractAuthenticationToken {

    private final ApplicationUser applicationUser;

    public JwtAuthenticatedUser(ApplicationUser applicationUser, SimpleGrantedAuthority authority) {
        super(Collections.singletonList(authority));
        this.applicationUser = applicationUser;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return applicationUser;
    }
}
