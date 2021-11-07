package pl.put.erasmusbackend.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import pl.put.erasmusbackend.configuration.ProjectProperties;
import pl.put.erasmusbackend.database.repository.UserRepository;

import java.util.List;

@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(proxyTargetClass = true)
public class SecurityConfiguration {

    private static final String[] WHITE_LIST = {"/v3/api-docs/**", "/documentation.html", "/documentation.yaml", "/webjars/**"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtSuccessHandler jwtSuccessHandler,
                                                         JwtFailureHandler jwtFailureHandler) {
        return http.formLogin()
                   .authenticationSuccessHandler(jwtSuccessHandler)
                   .authenticationFailureHandler(jwtFailureHandler)
                   .and()

                   // Stateless
                   .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                   .csrf().disable()

                   // Paths
                   .authorizeExchange()
                   .pathMatchers(WHITE_LIST).permitAll()
                   .pathMatchers("/login", "/register/**").permitAll()
                   .pathMatchers("/api/**").authenticated()
                   .and().build();
    }

    @Bean
    @Primary
    ReactiveAuthenticationManager reactiveAuthenticationManager(JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager,
                                                                UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager) {
        return new CustomReactiveAuthenticationManager(jwtReactiveAuthenticationManager, userDetailsRepositoryReactiveAuthenticationManager);
    }

    @Bean
    JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager(UserRepository userRepository, ProjectProperties projectProperties) {
        return new JwtReactiveAuthenticationManager(userRepository, projectProperties);
    }

    @Bean
    UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager(ReactiveUserDetailsService reactiveUserDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        manager.setPasswordEncoder(bCryptPasswordEncoder);
        return manager;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "DELETE", "PATCH", "OPTIONS", "PUT"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsWebFilter(source);
    }
}
