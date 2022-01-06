package com.erapulus.server.common.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Data
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "erapulus")
public class ErapulusProperties {

    private final JwtProperties jwt;
    private final LoginProperties login;
    private final AdministratorProperties administrator;

    @Data
    @AllArgsConstructor
    public static class JwtProperties {
        private final String issuer;
        private final String secret;
    }

    @Data
    @AllArgsConstructor
    public static class LoginProperties {
        private final String googleClientId;
    }

    @Data
    @AllArgsConstructor
    public static class AdministratorProperties {
        private final String firstName;
        private final String lastName;
        private final String email;
        private CharSequence password;
    }
}
