package com.erapulus.server.database;

import com.erapulus.server.database.model.DeviceEntity;
import com.erapulus.server.database.model.StudentEntity;
import com.erapulus.server.database.repository.DeviceRepository;
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
class DeviceRepositoryTest {

    private final static String STUDENT_1 = "student1";
    private final static String STUDENT_2 = "student2";
    private final static String DEVICE_ID_1 = "device1";
    private final static String DEVICE_ID_2 = "device2";
    private final static String DEVICE_ID_3 = "device3";
    private final static String DEVICE_ID_4 = "device4";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @AfterEach
    void clean() {
        deviceRepository.deleteAll().block();
        studentRepository.deleteAll().block();
    }

    @Test
    void findByStudentId_shouldReturnDevicesWhenDeviceExists() {
        // given
        var student1 = createStudent(STUDENT_1);
        var student2 = createStudent(STUDENT_2);
        var device1 = createDevice(DEVICE_ID_1, student1);
        var device2 = createDevice(DEVICE_ID_2, student1);
        var device3 = createDevice(DEVICE_ID_3, student2);
        var device4 = createDevice(DEVICE_ID_4, student2);

        // when
        Flux<DeviceEntity> result = deviceRepository.findAllByStudentId(student1.id());

        // then
        StepVerifier.create(result)
                    .recordWith(ArrayList::new)
                    .thenConsumeWhile(x -> true)
                    .expectRecordedMatches(posts -> posts.stream().map(DeviceEntity::id).toList().size() == 2)
                    .expectRecordedMatches(posts -> posts.stream().map(DeviceEntity::id).toList().containsAll(List.of(device1.id(), device2.id())))
                    .verifyComplete();
    }

    @Test
    void findByIdAndStudentId_shouldReturnDeviceWhenStudentAndIdExists() {
        // given
        var student1 = createStudent(STUDENT_1);
        var student2 = createStudent(STUDENT_2);
        var device1 = createDevice(DEVICE_ID_1, student1);
        var device2 = createDevice(DEVICE_ID_2, student2);

        // when
        Mono<DeviceEntity> result = deviceRepository.findByIdAndStudentId(device1.id(), student1.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(deviceFromDatabase -> assertEquals(device1.id(), deviceFromDatabase.id()))
                    .verifyComplete();
    }

    @Test
    void findByIdAndStudentId_shouldReturnEmptyMonoWhenWrongUser() {
        // given
        var student1 = createStudent(STUDENT_1);
        var student2 = createStudent(STUDENT_2);
        var device1 = createDevice(DEVICE_ID_1, student1);
        var device2 = createDevice(DEVICE_ID_2, student2);

        // when
        Mono<DeviceEntity> result = deviceRepository.findByIdAndStudentId(device1.id(), student2.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void findByIdAndStudentId_shouldReturnEmptyMonoWhenWrongId() {
        // given
        var student = createStudent(STUDENT_1);
        var device = createDevice(DEVICE_ID_1, student);

        // when
        Mono<DeviceEntity> result = deviceRepository.findByIdAndStudentId(device.id() + 1, student.id());

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    private DeviceEntity createDevice(String deviceId, StudentEntity student) {
        DeviceEntity deviceEntity = DeviceEntity.builder()
                                                .name("name")
                                                .deviceId(deviceId)
                                                .applicationUserId(student.id())
                                                .build();
        return deviceRepository.save(deviceEntity).block();
    }

    private StudentEntity createStudent(String email) {
        StudentEntity studentEntity = StudentEntity.builder()
                                                   .email(email)
                                                   .firstName("firstName")
                                                   .lastName("lastName")
                                                   .build();
        return studentRepository.save(studentEntity).block();
    }
}
