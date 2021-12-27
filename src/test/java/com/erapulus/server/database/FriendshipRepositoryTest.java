package com.erapulus.server.database;

import com.erapulus.server.database.model.*;
import com.erapulus.server.database.repository.EmployeeRepository;
import com.erapulus.server.database.repository.FriendshipRepository;
import com.erapulus.server.database.repository.StudentRepository;
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
class FriendshipRepositoryTest {

    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";
    private static final String EMAIL_3 = "example3@gmail.com";
    private static final String EMAIL_4 = "example4@gmail.com";
    private static final String EMAIL_5 = "example5@gmail.com";
    private static final String EMAIL_6 = "example6@gmail.com";
    private static final String FIRST_NAME_1 = "John";
    private static final String FIRST_NAME_2 = "Anne";
    private static final String LAST_NAME_1 = "JOHNSON";
    private static final String LAST_NAME_2 = "Smith";

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @AfterEach
    void clean() {
        friendshipRepository.deleteAll().block();
        studentRepository.deleteAll().block();
    }

    @Test
    void findFriendsByIdAndFilters_shouldReturnStudentEntityWhenFriendsFound() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var student3 = createStudent(EMAIL_3);
        var student4 = createStudent(EMAIL_4);
        var student5 = createStudent(EMAIL_5);
        var employee = createEmployee(EMAIL_6);
        createFriend(student1, student2, FriendshipStatus.ACCEPTED);
        createFriend(student1, student3, FriendshipStatus.ACCEPTED);
        createFriend(student1, student4, FriendshipStatus.REQUESTED);
        createFriend(student2, student5, FriendshipStatus.ACCEPTED);

        // when
        Flux<StudentEntity> result = friendshipRepository.findFriendsByIdAndFilters(student1.id(), null, 0, 5);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().size() == 2)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(student2.id(), student3.id())))
                    .verifyComplete();
    }

    @Test
    void findFriendsByIdAndFilters_shouldReturnStudentEntityWhenNameMatch() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2, FIRST_NAME_1, LAST_NAME_2);
        var student3 = createStudent(EMAIL_3, FIRST_NAME_2, LAST_NAME_1);
        var student4 = createStudent(EMAIL_4, FIRST_NAME_2, LAST_NAME_2);
        createFriend(student1, student2, FriendshipStatus.ACCEPTED);
        createFriend(student1, student3, FriendshipStatus.ACCEPTED);
        createFriend(student1, student4, FriendshipStatus.ACCEPTED);
        String commonPart = "ohn";

        // when
        Flux<StudentEntity> result = friendshipRepository.findFriendsByIdAndFilters(student1.id(), commonPart, 0, 5);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().size() == 2)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(student2.id(), student3.id())))
                    .verifyComplete();
    }

    @Test
    void findFriendsByIdAndFilters_shouldReturnStudentEntityFromSecondPage() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var student3 = createStudent(EMAIL_3);
        var student4 = createStudent(EMAIL_4);
        createFriend(student1, student2, FriendshipStatus.ACCEPTED);
        createFriend(student1, student3, FriendshipStatus.ACCEPTED);
        createFriend(student1, student4, FriendshipStatus.ACCEPTED);

        // when
        Flux<StudentEntity> result = friendshipRepository.findFriendsByIdAndFilters(student1.id(), null, 2, 2);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().contains(student4.id()))
                    .verifyComplete();
    }

    @Test
    void countFriendsByIdAndFilters_shouldReturnStudentEntityWhenFriendsFound() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var student3 = createStudent(EMAIL_3);
        var student4 = createStudent(EMAIL_4);
        var student5 = createStudent(EMAIL_5);
        var employee = createEmployee(EMAIL_6);
        createFriend(student1, student2, FriendshipStatus.ACCEPTED);
        createFriend(student1, student3, FriendshipStatus.ACCEPTED);
        createFriend(student1, student4, FriendshipStatus.REQUESTED);
        createFriend(student2, student5, FriendshipStatus.ACCEPTED);
        int expectedResult = 2;

        // when
        Mono<Integer> result = friendshipRepository.countFriendsByIdAndFilters(student1.id(), null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(userCount -> assertEquals(expectedResult, userCount))
                    .verifyComplete();
    }

    @Test
    void countFriendsByIdAndFilters_shouldReturnStudentEntityWhenNameMatch() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2, FIRST_NAME_1, LAST_NAME_2);
        var student3 = createStudent(EMAIL_3, FIRST_NAME_2, LAST_NAME_1);
        var student4 = createStudent(EMAIL_4, FIRST_NAME_2, LAST_NAME_2);
        createFriend(student1, student2, FriendshipStatus.ACCEPTED);
        createFriend(student1, student3, FriendshipStatus.ACCEPTED);
        createFriend(student1, student4, FriendshipStatus.ACCEPTED);
        String commonPart = "ohn";
        int expectedResult = 2;

        // when
        Mono<Integer> result = friendshipRepository.countFriendsByIdAndFilters(student1.id(), commonPart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(userCount -> assertEquals(expectedResult, userCount))
                    .verifyComplete();
    }

    @Test
    void findFriendRequestsById_shouldReturnStudentEntityWhenFriendsFound() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var student3 = createStudent(EMAIL_3);
        var student4 = createStudent(EMAIL_4);
        var student5 = createStudent(EMAIL_5);
        var student6 = createStudent(EMAIL_6);
        createFriend(student1, student2, FriendshipStatus.REQUESTED);
        createFriend(student1, student3, FriendshipStatus.REQUESTED);
        createFriend(student1, student4, FriendshipStatus.ACCEPTED);
        createFriend(student2, student5, FriendshipStatus.REQUESTED);
        createFriend(student2, student6, FriendshipStatus.ACCEPTED);

        // when
        Flux<StudentEntity> result = friendshipRepository.findFriendRequestsById(student1.id());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().size() == 2)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(student2.id(), student3.id())))
                    .verifyComplete();
    }

    @Test
    void findByUserIdAndFriendId_shouldReturnStudentEntityWhenFriendshipFound() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var student3 = createStudent(EMAIL_3);
        var student4 = createStudent(EMAIL_4);
        var friendship = createFriend(student1, student2, FriendshipStatus.REQUESTED);
        createFriend(student1, student3, FriendshipStatus.REQUESTED);
        createFriend(student2, student4, FriendshipStatus.ACCEPTED);

        // when
        Mono<FriendshipEntity> result = friendshipRepository.findByUserIdAndFriendId(student1.id(), student2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(friendResult -> assertEquals(friendship, friendResult))
                    .verifyComplete();
    }

    @Test
    void findByUserIdAndFriendId_shouldReturnEmptyWhenNoFriendship() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var student3 = createStudent(EMAIL_3);
        var student4 = createStudent(EMAIL_4);
        createFriend(student1, student3, FriendshipStatus.REQUESTED);
        createFriend(student2, student4, FriendshipStatus.ACCEPTED);

        // when
        Mono<FriendshipEntity> result = friendshipRepository.findByUserIdAndFriendId(student1.id(), student2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void deleteByUserIdAndFriendId_shouldDeleteTwoRows() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var student3 = createStudent(EMAIL_3);
        var student4 = createStudent(EMAIL_4);
        var friendship = createFriend(student1, student2, FriendshipStatus.REQUESTED);
        createFriend(student1, student3, FriendshipStatus.REQUESTED);
        createFriend(student2, student4, FriendshipStatus.ACCEPTED);
        int expected = 2;

        // when
        Mono<Integer> result = friendshipRepository.deleteByUserIdAndFriendId(student1.id(), student2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(deleteResult -> assertEquals(expected, deleteResult))
                    .verifyComplete();
    }

    @Test
    void findByUserIdAndFriendId_shouldDeleteZeroRows() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var student3 = createStudent(EMAIL_3);
        var student4 = createStudent(EMAIL_4);
        createFriend(student1, student3, FriendshipStatus.REQUESTED);
        createFriend(student2, student4, FriendshipStatus.ACCEPTED);
        int expected = 0;

        // when
        Mono<Integer> result = friendshipRepository.deleteByUserIdAndFriendId(student1.id(), student2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(deleteResult -> assertEquals(expected, deleteResult))
                    .verifyComplete();
    }

    private StudentEntity createStudent(String email) {
        StudentEntity student = StudentEntity.builder()
                                             .firstName("firstName")
                                             .lastName("lastName")
                                             .email(email)
                                             .build();
        return studentRepository.save(student).block();
    }

    private StudentEntity createStudent(String email, String firstName, String lastName) {
        StudentEntity student = StudentEntity.builder()
                                             .firstName(firstName)
                                             .lastName(lastName)
                                             .email(email)
                                             .build();
        return studentRepository.save(student).block();
    }

    private EmployeeEntity createEmployee(String email) {
        EmployeeEntity employee = EmployeeEntity.builder()
                                                .type(UserType.EMPLOYEE)
                                                .firstName("firstName")
                                                .lastName("lastName")
                                                .email(email)
                                                .build();
        return employeeRepository.save(employee).block();
    }

    private FriendshipEntity createFriend(StudentEntity user1, StudentEntity user2, FriendshipStatus status) {
        FriendshipEntity friendship1 = FriendshipEntity.builder()
                                                       .applicationUserId(user1.id())
                                                       .friendId(user2.id())
                                                       .status(status)
                                                       .build();
        var result = friendshipRepository.save(friendship1).block();
        FriendshipEntity friendship2 = FriendshipEntity.builder()
                                                       .applicationUserId(user2.id())
                                                       .friendId(user1.id())
                                                       .status(status)
                                                       .build();
        friendshipRepository.save(friendship2).block();
        return result;
    }
}
