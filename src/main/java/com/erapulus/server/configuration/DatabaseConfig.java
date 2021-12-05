package com.erapulus.server.configuration;

import com.erapulus.server.database.model.ApplicationUserEntity;
import com.erapulus.server.security.SecurityContextUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@Configuration
@EnableR2dbcAuditing
public class DatabaseConfig {

    @Bean
    ReactiveAuditorAware<Integer> auditorAware() {
        return () -> SecurityContextUtils.getUserAuthenticationData()
                                         .map(ApplicationUserEntity::id);
    }
}
