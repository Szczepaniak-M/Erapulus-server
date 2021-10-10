package pl.put.erasmusbackend.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Data
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {

    private final JwtProperties jwt;

    @Data
    @AllArgsConstructor
    public static class JwtProperties {
        private final String issuer;
        private final String secret;
    }
}
