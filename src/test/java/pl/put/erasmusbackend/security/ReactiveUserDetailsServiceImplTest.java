package pl.put.erasmusbackend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.put.erasmusbackend.database.model.Employee;
import pl.put.erasmusbackend.database.repository.EmployeeRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactiveUserDetailsServiceImplTest {

    private static final String EMAIL = "example@gamil.com";
    private static final String PASSWORD = "hashedPassword";

    @Mock
    private EmployeeRepository employeeRepository;

    private ReactiveUserDetailsServiceImpl reactiveUserDetailsService;

    @BeforeEach
    void setUp() {
        reactiveUserDetailsService = new ReactiveUserDetailsServiceImpl(employeeRepository);
    }

    @Test
    void findByUsername_shouldReturnUserDetailsWhenUserFound() {
        // given
        Employee employee = (Employee) new Employee().password(PASSWORD).email(EMAIL);
        when(employeeRepository.findByEmail(anyString())).thenReturn(Mono.just(employee));

        // when
        Mono<UserDetails> result = reactiveUserDetailsService.findByUsername(EMAIL);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(userDetails -> {
                        assertEquals(EMAIL, userDetails.getUsername());
                        assertEquals(PASSWORD, userDetails.getPassword());
                    })
                    .verifyComplete();

    }

    @Test
    void findByUsername_shouldReturnErrorWhenUserNotFound() {
        // given
        when(employeeRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        // when
        Mono<UserDetails> result = reactiveUserDetailsService.findByUsername(EMAIL);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(UsernameNotFoundException.class)
                    .verify();

    }
}
