package pl.put.erasmusbackend.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.put.erasmusbackend.database.model.ApplicationUser;
import pl.put.erasmusbackend.database.repository.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApplicationUserRepositoryTest {

    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();
    }

    @Test
    void findByEmail_shouldReturnUserEntityWhenUserFound() {
        // given
        var userEntity1 = createUser(EMAIL_1);
        var userEntity2 = createUser(EMAIL_2);
        userRepository.save(userEntity1);

        // when
        Mono<ApplicationUser> result = userRepository.findByEmail(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(userFromDatabase -> assertEquals(userEntity1.id(), userFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmptyMonoWhenNoUserFound() {
        // given
        // when
        Mono<ApplicationUser> result = userRepository.findByEmail(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();

    }

    private ApplicationUser createUser(String email) {
        ApplicationUser applicationUser = new ApplicationUser().type("applicationUser")
                                                               .firstName("firstName")
                                                               .lastName("lastName")
                                                               .email(email);
        return userRepository.save(applicationUser).block();
    }
}

