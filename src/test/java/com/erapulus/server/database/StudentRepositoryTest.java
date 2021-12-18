package com.erapulus.server.database;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.database.repository.StudentRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StudentRepositoryTest {

    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";
    private static final String EMAIL_3 = "example3@gmail.com";

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @AfterEach
    void setUp() {
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

    private StudentEntity createStudent(String email) {
        StudentEntity studentEntity = StudentEntity.builder()
                                                   .firstName("firstName")
                                                   .lastName("lastName")
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
}
