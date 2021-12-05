package com.erapulus.server.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.EmployeeRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EmployeeRepositoryTest {

    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll().block();
    }

    @Test
    void findByEmail_shouldReturnEmployeeEntityWhenEmployeeFound() {
        // given
        var employeeEntity1 = createEmployee(EMAIL_1);
        var employeeEntity2 = createEmployee(EMAIL_2);
        employeeRepository.save(employeeEntity1);

        // when
        Mono<EmployeeEntity> result = employeeRepository.findByEmail(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(employeeFromDatabase -> assertEquals(employeeEntity1.id(), employeeFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmptyMonoWhenNoEmployeeFound() {
        // given
        // when
        Mono<EmployeeEntity> result = employeeRepository.findByEmail(EMAIL_1);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();

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