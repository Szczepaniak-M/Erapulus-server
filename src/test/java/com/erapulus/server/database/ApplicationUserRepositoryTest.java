package com.erapulus.server.database;

import com.erapulus.server.database.model.ApplicationUserEntity;
import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.model.UniversityEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.UniversityRepository;
import com.erapulus.server.database.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApplicationUserRepositoryTest {

    private static final String FIRST_NAME_1 = "John";
    private static final String FIRST_NAME_2 = "Anne";
    private static final String LAST_NAME_1 = "Smith";
    private static final String LAST_NAME_2 = "JOHNSON";
    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";
    private static final String EMAIL_3 = "example3@gmail.com";
    private static final String UNIVERSITY_1 = "university1";
    private static final String UNIVERSITY_2 = "university2";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findByEmail_shouldReturnUserEntityWhenUserFound() {
        // given
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, null);
        var userEntity2 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_2, null);

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

    @Test
    void findByFilters_shouldReturnUserFromGivenUniversity() {
        // given
        var universityEntity1 = createUniversity(UNIVERSITY_1);
        var universityEntity2 = createUniversity(UNIVERSITY_2);
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, universityEntity1.id());
        var userEntity2 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.EMPLOYEE, EMAIL_2, universityEntity2.id());
        var userEntity3 = createUser(FIRST_NAME_1, LAST_NAME_2, UserType.ADMINISTRATOR, EMAIL_3, null);

        // when
        Flux<ApplicationUserEntity> result = userRepository.findByFilters(universityEntity1.id(), null, null, 0, 3);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().contains(userEntity1.id()))
                    .verifyComplete();
    }

    @Test
    void findByFilters_shouldReturnUserFromGivenUserType() {
        // given
        var universityEntity1 = createUniversity(UNIVERSITY_1);
        var universityEntity2 = createUniversity(UNIVERSITY_2);
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, universityEntity1.id());
        var userEntity2 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.EMPLOYEE, EMAIL_2, universityEntity2.id());
        var userEntity3 = createUser(FIRST_NAME_1, LAST_NAME_2, UserType.ADMINISTRATOR, EMAIL_3, null);

        // when
        Flux<ApplicationUserEntity> result = userRepository.findByFilters(null, UserType.EMPLOYEE, null, 0, 3);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().contains(userEntity2.id()))
                    .verifyComplete();
    }

    @Test
    void findByFilters_shouldReturnUserFromGivenFirstOrLastName() {
        // given
        var universityEntity1 = createUniversity(UNIVERSITY_1);
        var universityEntity2 = createUniversity(UNIVERSITY_2);
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, universityEntity1.id());
        var userEntity2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, universityEntity2.id());
        var userEntity3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);
        String commonPart = "ohn";

        // when
        Flux<ApplicationUserEntity> result = userRepository.findByFilters(null, null, commonPart, 0, 3);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().size() == 2)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(userEntity1.id(), userEntity2.id())))
                    .verifyComplete();
    }

    @Test
    void findByFilters_shouldReturnUserFromSecondPage() {
        // given
        var universityEntity1 = createUniversity(UNIVERSITY_1);
        var universityEntity2 = createUniversity(UNIVERSITY_2);
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, universityEntity1.id());
        var userEntity2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, universityEntity2.id());
        var userEntity3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);

        // when
        Flux<ApplicationUserEntity> result = userRepository.findByFilters(null, null, null, 2, 2);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().contains(userEntity3.id()))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldReturnUserFromGivenUniversity() {
        // given
        var universityEntity1 = createUniversity(UNIVERSITY_1);
        var universityEntity2 = createUniversity(UNIVERSITY_2);
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, universityEntity1.id());
        var userEntity2 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.EMPLOYEE, EMAIL_2, universityEntity2.id());
        var userEntity3 = createUser(FIRST_NAME_1, LAST_NAME_2, UserType.ADMINISTRATOR, EMAIL_3, null);
        int expectedResult = 1;

        // when
        Mono<Integer> result = userRepository.countByFilters(universityEntity1.id(), null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldReturnUserFromGivenUserType() {
        // given
        var universityEntity1 = createUniversity(UNIVERSITY_1);
        var universityEntity2 = createUniversity(UNIVERSITY_2);
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, universityEntity1.id());
        var userEntity2 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.EMPLOYEE, EMAIL_2, universityEntity2.id());
        var userEntity3 = createUser(FIRST_NAME_1, LAST_NAME_2, UserType.ADMINISTRATOR, EMAIL_3, null);
        int expectedResult = 1;

        // when
        Mono<Integer> result = userRepository.countByFilters(null, UserType.EMPLOYEE, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldReturnUserFromGivenFirstOrLastName() {
        // given
        var universityEntity1 = createUniversity(UNIVERSITY_1);
        var universityEntity2 = createUniversity(UNIVERSITY_2);
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, universityEntity1.id());
        var userEntity2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, universityEntity2.id());
        var userEntity3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);
        String commonPart = "ohn";
        int expectedResult = 2;

        // when
        Mono<Integer> result = userRepository.countByFilters(null, null, commonPart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldReturnAllUsers() {
        // given
        var universityEntity1 = createUniversity(UNIVERSITY_1);
        var universityEntity2 = createUniversity(UNIVERSITY_2);
        var userEntity1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, universityEntity1.id());
        var userEntity2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, universityEntity2.id());
        var userEntity3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);
        int expectedResult = 3;

        // when
        Mono<Integer> result = userRepository.countByFilters(null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }


    private ApplicationUserEntity createUser(String firstName, String lastName, UserType userType, String email, Integer universityId) {
        ApplicationUserEntity applicationUser = EmployeeEntity.builder()
                                                              .type(userType)
                                                              .firstName(firstName)
                                                              .lastName(lastName)
                                                              .email(email)
                                                              .universityId(universityId)
                                                              .build();
        return userRepository.save(applicationUser).block();
    }

    private UniversityEntity createUniversity(String name) {
        UniversityEntity university = UniversityEntity.builder()
                                                      .name(name)
                                                      .address("Address")
                                                      .zipcode("00000")
                                                      .city("city")
                                                      .country("country")
                                                      .websiteUrl("website")
                                                      .build();
        return universityRepository.save(university).block();
    }
}

