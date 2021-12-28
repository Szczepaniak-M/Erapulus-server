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
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static com.erapulus.server.database.model.UserType.*;
import static com.erapulus.server.web.common.CommonRequestVariable.STUDENT_PATH_PARAM;
import static com.erapulus.server.web.common.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.web.router.ApplicationUserRouter.USER_BASE_URL;
import static com.erapulus.server.web.router.ApplicationUserRouter.USER_DETAILS_URL;
import static com.erapulus.server.web.router.BuildingRouter.BUILDING_BASE_URL;
import static com.erapulus.server.web.router.BuildingRouter.BUILDING_DETAILS_URL;
import static com.erapulus.server.web.router.DeviceRouter.DEVICE_BASE_URL;
import static com.erapulus.server.web.router.DeviceRouter.DEVICE_DETAILS_URL;
import static com.erapulus.server.web.router.DocumentModuleRouter.DOCUMENT_MODULE_BASE_URL;
import static com.erapulus.server.web.router.DocumentModuleRouter.DOCUMENT_MODULE_DETAILS_URL;
import static com.erapulus.server.web.router.DocumentProgramRouter.DOCUMENT_PROGRAM_BASE_URL;
import static com.erapulus.server.web.router.DocumentProgramRouter.DOCUMENT_PROGRAM_DETAILS_URL;
import static com.erapulus.server.web.router.DocumentUniversityRouter.DOCUMENT_UNIVERSITY_BASE_URL;
import static com.erapulus.server.web.router.DocumentUniversityRouter.DOCUMENT_UNIVERSITY_DETAILS_URL;
import static com.erapulus.server.web.router.EmployeeRouter.EMPLOYEE_DETAILS_URL;
import static com.erapulus.server.web.router.EmployeeRouter.EMPLOYEE_LIST_URL;
import static com.erapulus.server.web.router.FacultyRouter.FACULTY_BASE_URL;
import static com.erapulus.server.web.router.FacultyRouter.FACULTY_DETAILS_URL;
import static com.erapulus.server.web.router.FriendshipRouter.*;
import static com.erapulus.server.web.router.ModuleRouter.MODULE_BASE_URL;
import static com.erapulus.server.web.router.ModuleRouter.MODULE_DETAILS_URL;
import static com.erapulus.server.web.router.PostRouter.POST_BASE_URL;
import static com.erapulus.server.web.router.PostRouter.POST_DETAILS_URL;
import static com.erapulus.server.web.router.ProgramRouter.PROGRAM_BASE_URL;
import static com.erapulus.server.web.router.ProgramRouter.PROGRAM_DETAILS_URL;
import static com.erapulus.server.web.router.RegisterRouter.*;
import static com.erapulus.server.web.router.StudentRouter.*;
import static com.erapulus.server.web.router.UniversityRouter.*;

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

        JwtReactiveAuthorizationManager onlyAdministratorAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(ADMINISTRATOR), null);
        JwtReactiveAuthorizationManager onlyAdministratorAndUniversityAdministratorManager = new JwtReactiveAuthorizationManager(List.of(ADMINISTRATOR, UNIVERSITY_ADMINISTRATOR), null);
        JwtReactiveAuthorizationManager onlyUniversityAdministratorManager = new JwtReactiveAuthorizationManager(List.of(UNIVERSITY_ADMINISTRATOR), null);
        JwtReactiveAuthorizationManager onlyUniversityAdministratorAndEmployeeWithParamValidationAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(EMPLOYEE, UNIVERSITY_ADMINISTRATOR), UNIVERSITY_PATH_PARAM);
        JwtReactiveAuthorizationManager onlyAdministratorAndUniversityAdministratorAndEmployeeWithParamValidationAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(ADMINISTRATOR, EMPLOYEE, UNIVERSITY_ADMINISTRATOR), UNIVERSITY_PATH_PARAM);
        JwtReactiveAuthorizationManager onlyAdministratorAndUniversityAdministratorAndEmployeeAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(ADMINISTRATOR, EMPLOYEE, UNIVERSITY_ADMINISTRATOR), null);
        JwtReactiveAuthorizationManager onlyUniversityAdministratorAndEmployeeAndStudentWithParamValidationAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(STUDENT, EMPLOYEE, UNIVERSITY_ADMINISTRATOR), UNIVERSITY_PATH_PARAM);
        JwtReactiveAuthorizationManager onlyStudentWithParamValidationAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(STUDENT), STUDENT_PATH_PARAM);
        JwtReactiveAuthorizationManager onlyAdministratorAndStudentAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(ADMINISTRATOR, STUDENT), null);
        JwtReactiveAuthorizationManager onlyStudentAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(STUDENT), null);
        JwtReactiveAuthorizationManager onlyUsersWithParamValidationAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(ADMINISTRATOR, UNIVERSITY_ADMINISTRATOR, EMPLOYEE, STUDENT), null);

        return http.formLogin().disable()
                   .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                   .csrf().disable()
                   .exceptionHandling()
                   .authenticationEntryPoint(authenticationFailureHandler)
                   .accessDeniedHandler(authorizationFailureHandler)
                   .and()

                   // Paths Matcher and Access
                   .authorizeExchange()
                   .pathMatchers(WHITE_LIST).permitAll()
                   .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                   .pathMatchers("/api/user/login/**", "/api/user/register/**").permitAll()

                   .matchers(onlyAdministratorPaths())
                   .access(onlyAdministratorAuthorizationManager)

                   .matchers(onlyAdministratorAndUniversityAdministratorPaths())
                   .access(onlyAdministratorAndUniversityAdministratorManager)

                   .matchers(onlyUniversityAdministratorPaths())
                   .access(onlyUniversityAdministratorManager)

                   .matchers(onlyUniversityAdministratorAndEmployeeWithParamValidationPaths())
                   .access(onlyUniversityAdministratorAndEmployeeWithParamValidationAuthorizationManager)

                   .matchers(onlyAdministratorAndUniversityAdministratorAndEmployeeWithParamValidationPaths())
                   .access(onlyAdministratorAndUniversityAdministratorAndEmployeeWithParamValidationAuthorizationManager)

                   .matchers(onlyAdministratorAndUniversityAdministratorAndEmployeePaths())
                   .access(onlyAdministratorAndUniversityAdministratorAndEmployeeAuthorizationManager)

                   .matchers(onlyUniversityAdministratorAndEmployeeAndStudentWithParamValidationPaths())
                   .access(onlyUniversityAdministratorAndEmployeeAndStudentWithParamValidationAuthorizationManager)

                   .matchers(onlyStudentWithParamValidationPaths())
                   .access(onlyStudentWithParamValidationAuthorizationManager)

                   .matchers(onlyAdministratorAndStudentPaths())
                   .access(onlyAdministratorAndStudentAuthorizationManager)

                   .matchers(onlyStudentPaths())
                   .access(onlyStudentAuthorizationManager)

                   .matchers(onlyUsersWithParamValidationPaths())
                   .access(onlyUsersWithParamValidationAuthorizationManager)

                   .pathMatchers("/api/**").denyAll()

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

    private ServerWebExchangeMatcher onlyAdministratorPaths() {
        ServerWebExchangeMatcher get = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,
                USER_BASE_URL);
        ServerWebExchangeMatcher post = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,
                REGISTER_ADMINISTRATOR,
                UNIVERSITY_BASE_URL);
        ServerWebExchangeMatcher delete = ServerWebExchangeMatchers.pathMatchers(HttpMethod.DELETE,
                UNIVERSITY_DETAILS_URL,
                USER_DETAILS_URL);
        return ServerWebExchangeMatchers.matchers(get, post, delete);
    }

    private ServerWebExchangeMatcher onlyAdministratorAndUniversityAdministratorPaths() {
        return ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,
                REGISTER_UNIVERSITY_ADMINISTRATOR);
    }

    private ServerWebExchangeMatcher onlyUniversityAdministratorPaths() {
        return ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,
                REGISTER_UNIVERSITY_EMPLOYEE);
    }

    private ServerWebExchangeMatcher onlyAdministratorAndUniversityAdministratorAndEmployeePaths() {
        ServerWebExchangeMatcher get = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,
                EMPLOYEE_DETAILS_URL);
        ServerWebExchangeMatcher put = ServerWebExchangeMatchers.pathMatchers(HttpMethod.PUT,
                EMPLOYEE_DETAILS_URL);
        return ServerWebExchangeMatchers.matchers(get, put);
    }

    private ServerWebExchangeMatcher onlyAdministratorAndUniversityAdministratorAndEmployeeWithParamValidationPaths() {
        ServerWebExchangeMatcher put = ServerWebExchangeMatchers.pathMatchers(HttpMethod.PUT,
                UNIVERSITY_DETAILS_URL);
        ServerWebExchangeMatcher patch = ServerWebExchangeMatchers.pathMatchers(HttpMethod.PATCH,
                UNIVERSITY_UPDATE_LOGO_URL);
        return ServerWebExchangeMatchers.matchers(put, patch);
    }

    private ServerWebExchangeMatcher onlyUniversityAdministratorAndEmployeeWithParamValidationPaths() {
        ServerWebExchangeMatcher get = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,
                EMPLOYEE_LIST_URL);
        ServerWebExchangeMatcher post = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,
                BUILDING_BASE_URL,
                DOCUMENT_MODULE_BASE_URL,
                DOCUMENT_PROGRAM_BASE_URL,
                DOCUMENT_UNIVERSITY_BASE_URL,
                FACULTY_BASE_URL,
                MODULE_BASE_URL,
                POST_BASE_URL,
                PROGRAM_BASE_URL);
        ServerWebExchangeMatcher put = ServerWebExchangeMatchers.pathMatchers(HttpMethod.PUT,
                BUILDING_DETAILS_URL,
                DOCUMENT_MODULE_DETAILS_URL,
                DOCUMENT_PROGRAM_DETAILS_URL,
                DOCUMENT_UNIVERSITY_DETAILS_URL,
                FACULTY_DETAILS_URL,
                MODULE_DETAILS_URL,
                POST_DETAILS_URL,
                PROGRAM_DETAILS_URL);
        ServerWebExchangeMatcher delete = ServerWebExchangeMatchers.pathMatchers(HttpMethod.DELETE,
                BUILDING_DETAILS_URL,
                DOCUMENT_MODULE_DETAILS_URL,
                DOCUMENT_PROGRAM_DETAILS_URL,
                DOCUMENT_UNIVERSITY_DETAILS_URL,
                FACULTY_DETAILS_URL,
                MODULE_DETAILS_URL,
                POST_DETAILS_URL,
                PROGRAM_DETAILS_URL);
        return ServerWebExchangeMatchers.matchers(get, post, put, delete);
    }

    private ServerWebExchangeMatcher onlyUniversityAdministratorAndEmployeeAndStudentWithParamValidationPaths() {
        return ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,
                BUILDING_BASE_URL, BUILDING_DETAILS_URL,
                DOCUMENT_MODULE_BASE_URL, DOCUMENT_MODULE_DETAILS_URL,
                DOCUMENT_PROGRAM_BASE_URL, DOCUMENT_PROGRAM_DETAILS_URL,
                DOCUMENT_UNIVERSITY_BASE_URL, DOCUMENT_UNIVERSITY_DETAILS_URL,
                FACULTY_BASE_URL, FACULTY_DETAILS_URL,
                MODULE_BASE_URL, MODULE_DETAILS_URL,
                POST_BASE_URL, POST_DETAILS_URL,
                PROGRAM_BASE_URL, PROGRAM_DETAILS_URL);
    }

    private ServerWebExchangeMatcher onlyStudentWithParamValidationPaths() {
        ServerWebExchangeMatcher get = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,
                DEVICE_BASE_URL, DEVICE_DETAILS_URL,
                FRIENDS_BASE_URL, FRIENDS_REQUESTS_URL);
        ServerWebExchangeMatcher post = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,
                DEVICE_BASE_URL,
                FRIENDS_BASE_URL, FRIENDS_DETAILS_URL);
        ServerWebExchangeMatcher put = ServerWebExchangeMatchers.pathMatchers(HttpMethod.PUT,
                DEVICE_DETAILS_URL,
                STUDENT_DETAILS_URL);
        ServerWebExchangeMatcher patch = ServerWebExchangeMatchers.pathMatchers(HttpMethod.PATCH,
                STUDENT_UPDATE_PHOTO_URL, STUDENT_UPDATE_UNIVERSITY_URL);
        ServerWebExchangeMatcher delete = ServerWebExchangeMatchers.pathMatchers(HttpMethod.DELETE,
                DEVICE_DETAILS_URL,
                FRIENDS_DETAILS_URL);
        return ServerWebExchangeMatchers.matchers(get, post, put, patch, delete);
    }

    private ServerWebExchangeMatcher onlyAdministratorAndStudentPaths() {
        return ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,
                UNIVERSITY_BASE_URL);
    }

    private ServerWebExchangeMatcher onlyUsersWithParamValidationPaths() {
        return ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,
                UNIVERSITY_DETAILS_URL);
    }

    private ServerWebExchangeMatcher onlyStudentPaths() {
        return ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET,
                STUDENT_BASE_URL, STUDENT_DETAILS_URL);
    }
}
