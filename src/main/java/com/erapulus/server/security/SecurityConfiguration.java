package com.erapulus.server.security;

import com.erapulus.server.configuration.ErapulusProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST = {"/v3/api-docs/**", "/documentation.html", "/documentation.yaml", "/webjars/**"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager,
                                                         CustomServerAuthenticationConverter customServerAuthenticationConverter,
                                                         AuthenticationFailureHandler authenticationFailureHandler,
                                                         AuthorizationFailureHandler authorizationFailureHandler) {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtReactiveAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(customServerAuthenticationConverter);
        authenticationWebFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        return http.formLogin().disable()
                   .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                   .csrf().disable()
                   .exceptionHandling()
                   .authenticationEntryPoint(authenticationFailureHandler)
                   .accessDeniedHandler(authorizationFailureHandler)
                   .and()


                   // Paths
                   .authorizeExchange()
                   .pathMatchers(WHITE_LIST).permitAll()
                   .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                   .pathMatchers("/api/user/login/**", "/api/user/register/**").permitAll()
                   .pathMatchers("/api/**").authenticated()
                   .and()
                   .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                   .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                   .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(ErapulusProperties erapulusProperties) {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(erapulusProperties.login().googleClientId()))
                .build();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "DELETE", "PATCH", "OPTIONS", "PUT"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsWebFilter(source);
    }
}
