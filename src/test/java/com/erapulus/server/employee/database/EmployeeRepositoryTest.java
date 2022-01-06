package com.erapulus.server.employee.database;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.database.StudentRepository;
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
class EmployeeRepositoryTest {

    private static final String EMAIL_1 = "example1@gmail.com";
    private static final String EMAIL_2 = "example2@gmail.com";
    private static final String EMAIL_3 = "example3@gmail.com";
    private static final String EMAIL_4 = "example4@gmail.com";
    private static final String EMAIL_5 = "example5@gmail.com";
    private static final String EMAIL_6 = "example6@gmail.com";
    private static final String UNIVERSITY_1 = "university1";
    private static final String UNIVERSITY_2 = "university2";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @AfterEach
    void clean() {
        employeeRepository.deleteAll().block();
        universityRepository.deleteAll().block();
    }

    @Test
    void findByEmailAndType_shouldReturnEmployeeWhenEmployeeFound() {
        // given
        var employee1 = createEmployee(EMAIL_1, UserType.EMPLOYEE, null);
        var employee2 = createEmployee(EMAIL_2, UserType.EMPLOYEE, null);
        var student = createStudent(EMAIL_3, null);

        // when
        Mono<EmployeeEntity> result = employeeRepository.findByEmailAndType(EMAIL_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(employeeFromDatabase -> assertEquals(employee1.id(), employeeFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByEmailAndType_shouldReturnEmptyMonoWhenWrongType() {
        // given
        var student = createStudent(EMAIL_1, null);

        // when
        Mono<EmployeeEntity> result = employeeRepository.findByEmailAndType(EMAIL_1);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findByEmailAndType_shouldReturnEmptyMonoWhenWrongEmail() {
        // given
        var employee = createEmployee(EMAIL_1, UserType.EMPLOYEE, null);

        // when
        Mono<EmployeeEntity> result = employeeRepository.findByEmailAndType(EMAIL_2);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findByIdAndType_shouldReturnEmployeeWhenEmployeeFound() {
        // given
        var employee1 = createEmployee(EMAIL_1, UserType.EMPLOYEE, null);
        var employee2 = createEmployee(EMAIL_2, UserType.EMPLOYEE, null);
        var student = createStudent(EMAIL_3, null);

        // when
        Mono<EmployeeEntity> result = employeeRepository.findByIdAndType(employee1.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(employeeFromDatabase -> assertEquals(employee1.id(), employeeFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByIdAndType_shouldReturnEmptyMonoWhenWrongType() {
        // given
        var student = createStudent(EMAIL_1, null);

        // when
        Mono<EmployeeEntity> result = employeeRepository.findByIdAndType(student.id());

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findByIdAndType_shouldReturnEmptyMonoWhenId() {
        // given
        var employee = createEmployee(EMAIL_1, UserType.EMPLOYEE, null);

        // when
        Mono<EmployeeEntity> result = employeeRepository.findByIdAndType(employee.id() + 10);

        //then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findAllByUniversityIdAndType_shouldReturnEmployeeFromGivenUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var employee1 = createEmployee(EMAIL_1, UserType.EMPLOYEE, university1.id());
        var employee2 = createEmployee(EMAIL_2, UserType.EMPLOYEE, university2.id());
        var employee3 = createEmployee(EMAIL_3, UserType.UNIVERSITY_ADMINISTRATOR, university1.id());
        var employee4 = createEmployee(EMAIL_4, UserType.UNIVERSITY_ADMINISTRATOR, university2.id());
        var employee5 = createEmployee(EMAIL_5, UserType.ADMINISTRATOR, null);
        var student = createStudent(EMAIL_6, university1.id());

        // when
        Flux<EmployeeEntity> result = employeeRepository.findAllByUniversityIdAndType(university1.id());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(employees -> employees.stream().map(EmployeeEntity::id).toList().size() == 2)
                    .expectRecordedMatches(employees -> employees.stream().map(EmployeeEntity::id).toList().containsAll(List.of(employee1.id(), employee3.id())))
                    .verifyComplete();
    }

    @Test
    void deleteAllByUniversityId_shouldDeleteEmployeeFromGivenUniversity() {
        // given
        var university1 = createUniversity(UNIVERSITY_1);
        var university2 = createUniversity(UNIVERSITY_2);
        var employee1 = createEmployee(EMAIL_1, UserType.EMPLOYEE, university1.id());
        var employee2 = createEmployee(EMAIL_2, UserType.EMPLOYEE, university2.id());
        var employee3 = createEmployee(EMAIL_3, UserType.UNIVERSITY_ADMINISTRATOR, university1.id());
        var employee4 = createEmployee(EMAIL_4, UserType.UNIVERSITY_ADMINISTRATOR, university2.id());
        var employee5 = createEmployee(EMAIL_5, UserType.ADMINISTRATOR, null);
        var student = createStudent(EMAIL_6, university1.id());

        // when
        Mono<Void> result = employeeRepository.deleteAllByUniversityId(university1.id());

        // then
        StepVerifier.create(employeeRepository.findAll())
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(devices -> devices.stream().map(ApplicationUserEntity::id).toList().size() == 6)
                    .expectRecordedMatches(devices -> devices.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(employee1.id(), employee2.id(), employee3.id(), employee4.id(), employee5.id(), student.id())))
                    .verifyComplete();

        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();

        StepVerifier.create(employeeRepository.findAll())
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(devices -> devices.stream().map(ApplicationUserEntity::id).toList().size() == 4)
                    .expectRecordedMatches(devices -> devices.stream().map(ApplicationUserEntity::id).toList().containsAll(List.of(employee2.id(), employee4.id(), employee5.id(), student.id())))
                    .verifyComplete();
    }

    private EmployeeEntity createEmployee(String email, UserType userType, Integer university) {
        EmployeeEntity employee = EmployeeEntity.builder()
                                                .type(userType)
                                                .firstName("firstName")
                                                .lastName("lastName")
                                                .email(email)
                                                .universityId(university)
                                                .build();
        return employeeRepository.save(employee).block();
    }

    private StudentEntity createStudent(String email, Integer university) {
        StudentEntity student = StudentEntity.builder()
                                             .firstName("firstName")
                                             .lastName("lastName")
                                             .email(email)
                                             .universityId(university)
                                             .build();
        return studentRepository.save(student).block();
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
