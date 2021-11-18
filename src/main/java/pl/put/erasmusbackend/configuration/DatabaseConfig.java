package pl.put.erasmusbackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import pl.put.erasmusbackend.database.model.ApplicationUserEntity;
import pl.put.erasmusbackend.security.SecurityContextUtils;

@Configuration
@EnableR2dbcAuditing
public class DatabaseConfig {

    @Bean
    ReactiveAuditorAware<Integer> auditorAware() {
        return () -> SecurityContextUtils.getUserAuthenticationData()
                                         .map(ApplicationUserEntity::id);
    }
}
