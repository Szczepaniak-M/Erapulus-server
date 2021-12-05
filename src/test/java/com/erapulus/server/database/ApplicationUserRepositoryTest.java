package com.erapulus.server.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.erapulus.server.database.model.ApplicationUserEntity;
import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.UserRepository;
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
        Mono<ApplicationUserEntity> result = userRepository.findByEmail(EMAIL_1);

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
        Mono<ApplicationUserEntity> result = userRepository.findByEmail(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();

    }

    private ApplicationUserEntity createUser(String email) {
        ApplicationUserEntity applicationUser = EmployeeEntity.builder()
                                                              .type(UserType.EMPLOYEE)
                                                              .firstName("firstName")
                                                              .lastName("lastName")
                                                              .email(email)
                                                              .build();
        return userRepository.save(applicationUser).block();
    }
}

