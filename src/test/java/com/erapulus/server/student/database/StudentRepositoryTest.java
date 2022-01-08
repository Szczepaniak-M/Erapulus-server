package com.erapulus.server.student.database;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.database.EmployeeRepository;
import com.erapulus.server.friendship.database.FriendshipRepository;
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
class StudentRepositoryTest {

    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";
    private static final String EMAIL_3 = "example3@gmail.com";
    private static final String EMAIL_4 = "example4@gmail.com";
    private static final String EMAIL_5 = "example5@gmail.com";
    private static final String FIRST_NAME_1 = "John";
    private static final String FIRST_NAME_2 = "Anne";
    private static final String LAST_NAME_1 = "JOHNSON";
    private static final String LAST_NAME_2 = "Smith";
    private static final String UNIVERSITY_1 = "university1";
    private static final String UNIVERSITY_2 = "university2";


    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        friendshipRepository.deleteAll().block();
        studentRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findAllByName_shouldReturnStudentsEntity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);

        var student1 = createStudent(EMAIL_1, FIRST_NAME_1, LAST_NAME_1, university1.id());
        var student2 = createStudent(EMAIL_2, FIRST_NAME_1, LAST_NAME_2, university1.id());
        var student3 = createStudent(EMAIL_3, FIRST_NAME_2, LAST_NAME_1, university2.id());
        var student4 = createStudent(EMAIL_4, FIRST_NAME_2, LAST_NAME_2, null);
        var employee = createEmployee(EMAIL_5);
        String commonPart = "ohn";

        // when
        Flux<StudentEntity> result = studentRepository.findAllByNameAndUniversityId(commonPart, university1.id());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().size() == 2)
                    .expectRecordedMatches(users -> users.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(student1.id(), student2.id())))
                    .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnStudentEntityWhenStudentFound() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var employee = createEmployee(EMAIL_3);

        // when
        Mono<StudentEntity> result = studentRepository.findByEmail(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentFromDatabase -> assertEquals(student1.id(), studentFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmptyMonoWhenNoStudentFound() {
        // given
        var employee = createEmployee(EMAIL_1);

        // when
        Mono<StudentEntity> result = studentRepository.findByEmail(EMAIL_1);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findByIdAndType_shouldReturnStudentEntityWhenStudentFound() {
        // given
        var student1 = createStudent(EMAIL_1);
        var student2 = createStudent(EMAIL_2);
        var employee = createEmployee(EMAIL_3);

        // when
        Mono<StudentEntity> result = studentRepository.findByIdAndType(student2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(studentFromDatabase -> assertEquals(student2.id(), studentFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByIdAndType_shouldReturnEmptyMonoWhenNoStudentFound() {
        // given
        var student = createStudent(EMAIL_1);
        var employee = createEmployee(EMAIL_2);

        // when
        Mono<StudentEntity> result = studentRepository.findByIdAndType(employee.id());

        //then
        StepVerifier.create(result)
                    .expectSubscription()
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

    private StudentEntity createStudent(String email, String firstName, String lastName, Integer university) {
        StudentEntity student = StudentEntity.builder()
                                             .firstName(firstName)
                                             .lastName(lastName)
                                             .email(email)
                                             .universityId(university)
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

    private UniversityEntity createUniversity(String name) {
        UniversityEntity universityEntity = UniversityEntity.builder()
                                                            .name(name)
                                                            .address("Some address")
                                                            .zipcode("00000")
                                                            .city("city")
                                                            .country("country")
                                                            .websiteUrl("url")
                                                            .build();
        return universityRepository.save(universityEntity).block();
    }
}
