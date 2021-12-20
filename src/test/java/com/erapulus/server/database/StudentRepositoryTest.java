package com.erapulus.server.database;

import com.erapulus.server.database.model.*;
import com.erapulus.server.database.repository.EmployeeRepository;
import com.erapulus.server.database.repository.FriendshipRepository;
import com.erapulus.server.database.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.sql.In;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StudentRepositoryTest {

    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";
    private static final String EMAIL_3 = "example3@gmail.com";
    private static final String EMAIL_4 = "example4@gmail.com";
    private static final String EMAIL_5 = "example5@gmail.com";
    private static final String EMAIL_6 = "example6@gmail.com";
    private static final String EMAIL_7 = "example7@gmail.com";

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
    void findByEmail_shouldReturnStudentEntityWhenStudentFound() {
        // given
        var studentEntity1 = createStudent(EMAIL_1);
        var studentEntity2 = createStudent(EMAIL_2);
        var employeeEntity = createEmployee(EMAIL_3);

        // when
        Mono<StudentEntity> result = studentRepository.findByEmail(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentFromDatabase -> assertEquals(studentEntity1.id(), studentFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmptyMonoWhenNoStudentFound() {
        // given
        var employeeEntity = createEmployee(EMAIL_1);

        // when
        Mono<StudentEntity> result = studentRepository.findByEmail(EMAIL_1);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findById_shouldReturnStudentEntityWhenStudentFound() {
        // given
        var studentEntity1 = createStudent(EMAIL_1);
        var studentEntity2 = createStudent(EMAIL_2);
        var employeeEntity = createEmployee(EMAIL_3);

        // when
        Mono<StudentEntity> result = studentRepository.findByIdAndType(studentEntity2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentFromDatabase -> assertEquals(studentEntity2.id(), studentFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmptyMonoWhenNoStudentFound() {
        // given
        var studentEntity1 = createStudent(EMAIL_1);
        var employeeEntity = createEmployee(EMAIL_2);

        // when
        Mono<StudentEntity> result = studentRepository.findByIdAndType(employeeEntity.id());

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findFriendsByIdAndFilters_shouldReturnStudentEntityWhenFriendsFound() {
        // given
        var studentEntity1 = createStudent(EMAIL_1);
        var studentEntity2 = createStudent(EMAIL_2);
        var studentEntity3 = createStudent(EMAIL_3);
        var studentEntity4 = createStudent(EMAIL_4);
        var studentEntity5 = createStudent(EMAIL_5);
        var employeeEntity = createEmployee(EMAIL_6);
        createFriend(studentEntity1, studentEntity2, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity3, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity4, FriendshipStatus.REQUESTED);
        createFriend(studentEntity2, studentEntity5, FriendshipStatus.ACCEPTED);

        // when
        Flux<StudentEntity> result = studentRepository.findFriendsByIdAndFilters(studentEntity1.id(), null, 0, 5);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().size() == 2)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(studentEntity2.id(), studentEntity3.id())))
                    .verifyComplete();
    }

    @Test
    void findFriendsByIdAndFilters_shouldReturnStudentEntityWhenNameMatch() {
        // given
        var studentEntity1 = createStudent(EMAIL_1);
        var studentEntity2 = createStudent(EMAIL_2, "Johnson", "Smith");
        var studentEntity3 = createStudent(EMAIL_3, "Anne", "Johnson");
        var studentEntity4 = createStudent(EMAIL_4, "Anne", "Smith");
        createFriend(studentEntity1, studentEntity2, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity3, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity4, FriendshipStatus.ACCEPTED);
        String commonPart = "john";

        // when
        Flux<StudentEntity> result = studentRepository.findFriendsByIdAndFilters(studentEntity1.id(), commonPart, 0, 5);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().size() == 2)
                    .expectRecordedMatches(students -> students.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(studentEntity2.id(), studentEntity3.id())))
                    .verifyComplete();
    }

    @Test
    void findFriendsByIdAndFilters_shouldReturnStudentEntityFromSecondPage() {
        // given
        var studentEntity1 = createStudent(EMAIL_1);
        var studentEntity2 = createStudent(EMAIL_2);
        var studentEntity3 = createStudent(EMAIL_3);
        var studentEntity4 = createStudent(EMAIL_4);
        createFriend(studentEntity1, studentEntity2, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity3, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity4, FriendshipStatus.ACCEPTED);

        // when
        Flux<StudentEntity> result = studentRepository.findFriendsByIdAndFilters(studentEntity1.id(), null, 2, 2);

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().size() == 1)
                    .expectRecordedMatches(posts -> posts.stream().map(ApplicationUserEntity::id).toList().contains(studentEntity4.id()))
                    .verifyComplete();
    }

    @Test
    void countFriendsByIdAndFilters_shouldReturnStudentEntityWhenFriendsFound() {
        // given
        var studentEntity1 = createStudent(EMAIL_1);
        var studentEntity2 = createStudent(EMAIL_2);
        var studentEntity3 = createStudent(EMAIL_3);
        var studentEntity4 = createStudent(EMAIL_4);
        var studentEntity5 = createStudent(EMAIL_5);
        var employeeEntity = createEmployee(EMAIL_6);
        createFriend(studentEntity1, studentEntity2, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity3, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity4, FriendshipStatus.REQUESTED);
        createFriend(studentEntity2, studentEntity5, FriendshipStatus.ACCEPTED);
        int expectedResult = 2;

        // when
        Mono<Integer> result = studentRepository.countFriendsByIdAndFilters(studentEntity1.id(), null);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    @Test
    void countFriendsByIdAndFilters_shouldReturnStudentEntityWhenNameMatch() {
        // given
        var studentEntity1 = createStudent(EMAIL_1);
        var studentEntity2 = createStudent(EMAIL_2, "Johnson", "Smith");
        var studentEntity3 = createStudent(EMAIL_3, "Anne", "Johnson");
        var studentEntity4 = createStudent(EMAIL_4, "Anne", "Smith");
        createFriend(studentEntity1, studentEntity2, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity3, FriendshipStatus.ACCEPTED);
        createFriend(studentEntity1, studentEntity4, FriendshipStatus.ACCEPTED);
        String commonPart = "john";
        int expectedResult = 2;

        // when
        Mono<Integer> result = studentRepository.countFriendsByIdAndFilters(studentEntity1.id(), commonPart);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(postCount -> assertEquals(expectedResult, postCount))
                    .verifyComplete();
    }

    private StudentEntity createStudent(String email) {
        StudentEntity studentEntity = StudentEntity.builder()
                                                   .firstName("firstName")
                                                   .lastName("lastName")
                                                   .email(email)
                                                   .build();
        return studentRepository.save(studentEntity).block();
    }

    private StudentEntity createStudent(String email, String firstName, String lastName) {
        StudentEntity studentEntity = StudentEntity.builder()
                                                   .firstName(firstName)
                                                   .lastName(lastName)
                                                   .email(email)
                                                   .build();
        return studentRepository.save(studentEntity).block();
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

    private void createFriend(StudentEntity user1, StudentEntity user2, FriendshipStatus status) {
        FriendshipEntity friendship1 = FriendshipEntity.builder()
                                                       .applicationUserId(user1.id())
                                                       .friendId(user2.id())
                                                       .status(status)
                                                       .build();
        friendshipRepository.save(friendship1).block();
        FriendshipEntity friendship2 = FriendshipEntity.builder()
                                                       .applicationUserId(user2.id())
                                                       .friendId(user1.id())
                                                       .status(status)
                                                       .build();
        friendshipRepository.save(friendship2).block();
    }
}
