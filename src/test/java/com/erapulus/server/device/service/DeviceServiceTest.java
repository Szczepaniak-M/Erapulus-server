package com.erapulus.server.device.service;


import com.erapulus.server.device.database.DeviceEntity;
import com.erapulus.server.device.database.DeviceRepository;
import com.erapulus.server.device.dto.DeviceRequestDto;
import com.erapulus.server.device.dto.DeviceResponseDto;
import com.erapulus.server.device.mapper.DeviceEntityToResponseDtoMapper;
import com.erapulus.server.device.mapper.DeviceRequestDtoToEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    public static final int STUDENT_ID = 1;
    private final static int ID_1 = 1;
    private final static int ID_2 = 2;

    @Mock
    DeviceRepository deviceRepository;

    DeviceService deviceService;


    @BeforeEach
    void setUp() {
        deviceService = new DeviceService(deviceRepository,
                new DeviceRequestDtoToEntityMapper(),
                new DeviceEntityToResponseDtoMapper());
    }

    @Test
    void listDevices_shouldReturnDeviceList() {
        // given
        var device1 = createDevice(ID_1);
        var device2 = createDevice(ID_2);
        when(deviceRepository.findAllByStudentId(STUDENT_ID)).thenReturn(Flux.just(device1, device2));

        // when
        Mono<List<DeviceResponseDto>> result = deviceService.listDevices(STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(devices -> assertEquals(2, devices.size()))
                    .verifyComplete();
    }

    @Test
    void createDevice_shouldCreateDevice() {
        // given
        var deviceRequestDto = new DeviceRequestDto();
        when(deviceRepository.save(any(DeviceEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, DeviceEntity.class).id(ID_1)));

        // when
        Mono<DeviceResponseDto> result = deviceService.createDevice(deviceRequestDto, STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(device -> {
                        assertEquals(ID_1, device.id());
                        assertEquals(STUDENT_ID, device.applicationUserId());
                    })
                    .verifyComplete();
    }

    @Test
    void getDeviceById_shouldReturnDeviceWhenFound() {
        // given
        var device = createDevice(ID_1);
        when(deviceRepository.findByIdAndStudentId(ID_1, STUDENT_ID)).thenReturn(Mono.just(device));

        // when
        Mono<DeviceResponseDto> result = deviceService.getDeviceById(ID_1, STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(deviceResponseDto -> {
                        assertEquals(ID_1, deviceResponseDto.id());
                        assertEquals(STUDENT_ID, deviceResponseDto.applicationUserId());
                    })
                    .verifyComplete();
    }

    @Test
    void getDeviceById_shouldThrowExceptionWhenDeviceNotFound() {
        // given
        when(deviceRepository.findByIdAndStudentId(ID_1, STUDENT_ID)).thenReturn(Mono.empty());

        // when
        Mono<DeviceResponseDto> result = deviceService.getDeviceById(ID_1, STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateDevice_shouldUpdateDeviceWhenFound() {
        // given
        var device = createDevice(ID_1);
        var deviceRequestDto = new DeviceRequestDto();
        when(deviceRepository.findByIdAndStudentId(ID_1, STUDENT_ID)).thenReturn(Mono.just(device));
        when(deviceRepository.save(any(DeviceEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, DeviceEntity.class).id(ID_1)));

        // when
        Mono<DeviceResponseDto> result = deviceService.updateDevice(deviceRequestDto, ID_1, STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(deviceResponseDto -> {
                        assertEquals(ID_1, deviceResponseDto.id());
                        assertEquals(STUDENT_ID, deviceResponseDto.applicationUserId());
                    })
                    .verifyComplete();
    }

    @Test
    void updateDevice_shouldThrowExceptionWhenDeviceNotFound() {
        // given
        var deviceRequestDto = new DeviceRequestDto();
        when(deviceRepository.findByIdAndStudentId(ID_1, STUDENT_ID)).thenReturn(Mono.empty());

        // when
        Mono<DeviceResponseDto> result = deviceService.updateDevice(deviceRequestDto, ID_1, STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteDevice_shouldDeleteDeviceWhenFound() {
        // given
        var device = createDevice(ID_1);
        when(deviceRepository.findByIdAndStudentId(ID_1, STUDENT_ID)).thenReturn(Mono.just(device));
        when(deviceRepository.deleteById(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = deviceService.deleteDevice(ID_1, STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(Assertions::assertTrue)
                    .verifyComplete();
    }

    @Test
    void deleteDevice_shouldThrowExceptionWhenDeviceNotFound() {
        // given
        when(deviceRepository.findByIdAndStudentId(ID_1, STUDENT_ID)).thenReturn(Mono.empty());

        // when
        Mono<Boolean> result = deviceService.deleteDevice(ID_1, STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllDevicesByUniversityId() {
        // when
        when(deviceRepository.deleteAllByStudentId(STUDENT_ID)).thenReturn(Mono.empty());

        // given
        Mono<Void> result = deviceService.deleteAllDevicesByStudentId(STUDENT_ID);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    private DeviceEntity createDevice(int id) {
        return DeviceEntity.builder()
                           .id(id)
                           .name("name")
                           .deviceId("deviceId")
                           .applicationUserId(STUDENT_ID)
                           .build();
    }
}