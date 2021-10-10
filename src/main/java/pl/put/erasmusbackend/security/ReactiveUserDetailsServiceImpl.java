package pl.put.erasmusbackend.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.put.erasmusbackend.database.repository.EmployeeRepository;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@AllArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return employeeRepository.findByEmail(email)
                                 .map(user -> User.builder()
                                                  .username(user.email())
                                                  .password(user.password())
                                                  .authorities(Collections.emptyList())
                                                  .build())
                                 .switchIfEmpty(Mono.error(new UsernameNotFoundException(email)));
    }
}
