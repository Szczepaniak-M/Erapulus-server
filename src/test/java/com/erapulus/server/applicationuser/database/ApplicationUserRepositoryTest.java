package com.erapulus.server.applicationuser.database;

import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.database.UniversityRepository;
import org.junit.jupiter.api.AfterEach;
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

    private static final String FIRST_NAME_1 = "Johnson";
    private static final String FIRST_NAME_2 = "Anne";
    private static final String LAST_NAME_1 = "Smith";
    private static final String LAST_NAME_2 = "JOHNSON";
    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";
    private static final String EMAIL_3 = "example3@gmail.com";
    private static final String UNIVERSITY_1 = "university1";
    private static final String UNIVERSITY_2 = "university2";

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        applicationUserRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findByEmail_shouldReturnUserWhenUserFound() {
        // given
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, null);
        var user2 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_2, null);

        // when
        Mono<ApplicationUserEntity> result = applicationUserRepository.findByEmail(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(userFromDatabase -> assertEquals(user1.id(), userFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmptyMonoWhenNoUserFound() {
        // given
        // when
        Mono<ApplicationUserEntity> result = applicationUserRepository.findByEmail(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findByFilters_shouldReturnUserWhenUniversityGiven() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_1, LAST_NAME_2, UserType.ADMINISTRATOR, EMAIL_3, null);

        // when
        Flux<ApplicationUserEntity> result = applicationUserRepository.findAllByFilters(university1.id(), null, null, null, 0, 3);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().contains(user1.id()))
                    .verifyComplete();
    }

    @Test
    void findByFilters_shouldReturnUserWhenUserTypeGiven() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_1, LAST_NAME_2, UserType.ADMINISTRATOR, EMAIL_3, null);

        // when
        Flux<ApplicationUserEntity> result = applicationUserRepository.findAllByFilters(null, UserType.EMPLOYEE, null, null, 0, 3);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().contains(user2.id()))
                    .verifyComplete();
    }

    @Test
    void findByFilters_shouldReturnUserWhenNameGiven() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);
        String commonPart = "ohn";

        // when
        Flux<ApplicationUserEntity> result = applicationUserRepository.findAllByFilters(null, null, commonPart, null, 0, 3);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().size() == 2)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(user1.id(), user2.id())))
                    .verifyComplete();
    }

    @Test
    void findByFilters_shouldReturnUserWhenEmailGiven() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);
        String emailPart = "example3";

        // when
        Flux<ApplicationUserEntity> result = applicationUserRepository.findAllByFilters(null, null, null, emailPart, 0, 3);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().contains(user3.id()))
                    .verifyComplete();
    }

    @Test
    void findByFilters_shouldReturnUserWhenSecondPageRequested() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);

        // when
        Flux<ApplicationUserEntity> result = applicationUserRepository.findAllByFilters(null, null, null, null, 2, 2);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().contains(user3.id()))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldCountUserWhenUniversityGiven() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_1, LAST_NAME_2, UserType.ADMINISTRATOR, EMAIL_3, null);
        int expectedResult = 1;

        // when
        Mono<Integer> result = applicationUserRepository.countByFilters(university1.id(), null, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldCountUserWhenUserTypeGiven() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_1, LAST_NAME_2, UserType.ADMINISTRATOR, EMAIL_3, null);
        int expectedResult = 1;

        // when
        Mono<Integer> result = applicationUserRepository.countByFilters(null, UserType.EMPLOYEE, null, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldReturnUserWhenNameGiven() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);
        String commonPart = "john";
        int expectedResult = 2;

        // when
        Mono<Integer> result = applicationUserRepository.countByFilters(null, null, commonPart, null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldReturnUserWhenEmailGiven() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);
        String commonEmail = "example2";
        int expectedResult = 1;

        // when
        Mono<Integer> result = applicationUserRepository.countByFilters(null, null, null, commonEmail);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void countByFilters_shouldCountAllUsers() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var user1 = createUser(FIRST_NAME_1, LAST_NAME_1, UserType.STUDENT, EMAIL_1, university1.id());
        var user2 = createUser(FIRST_NAME_2, LAST_NAME_2, UserType.EMPLOYEE, EMAIL_2, university2.id());
        var user3 = createUser(FIRST_NAME_2, LAST_NAME_1, UserType.ADMINISTRATOR, EMAIL_3, null);
        int expectedResult = 3;

        // when
        Mono<Integer> result = applicationUserRepository.countByFilters(null, null, null, null);

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
        return applicationUserRepository.save(applicationUser).block();
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

