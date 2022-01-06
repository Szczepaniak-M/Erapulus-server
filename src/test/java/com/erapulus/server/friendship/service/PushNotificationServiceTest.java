package com.erapulus.server.friendship.service;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.applicationuser.database.ApplicationUserRepository;
import com.erapulus.server.device.database.DeviceEntity;
import com.erapulus.server.device.database.DeviceRepository;
import com.erapulus.server.friendship.dto.FriendshipResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PushNotificationServiceTest {

    private static final int STUDENT_ID = 1;
    private static final int FRIEND_ID = 2;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private FCMService fcmService;

    private PushNotificationService pushNotificationService;

    @BeforeEach
    void setUp() {
        this.pushNotificationService = new PushNotificationService(deviceRepository, applicationUserRepository, fcmService);
    }

    @Test
    void sendPushNotification() {
        // given
        var user = createUser();
        var device1 = createDevice();
        var device2 = createDevice();
        var friendResponseDto = createFriendshipResponseDto();

        when(deviceRepository.findAllByStudentId(STUDENT_ID)).thenReturn(Flux.just(device1, device2));
        when(applicationUserRepository.findById(FRIEND_ID)).thenReturn(Mono.just(user));
        when(fcmService.sendPushNotification(anyString(), any(ApplicationUserEntity.class))).thenReturn(Mono.empty());

        // when
        Mono<Void> result = pushNotificationService.sendPushNotification(friendResponseDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
        verify(fcmService, times(2)).sendPushNotification(anyString(), any(ApplicationUserEntity.class));
    }

    private FriendshipResponseDto createFriendshipResponseDto() {
        return FriendshipResponseDto.builder()
                                    .applicationUserId(STUDENT_ID)
                                    .friendId(FRIEND_ID)
                                    .build();
    }

    private ApplicationUserEntity createUser() {
        return ApplicationUserEntity.builder()
                                    .id(FRIEND_ID)
                                    .firstName("name")
                                    .lastName("deviceId")
                                    .build();
    }

    private DeviceEntity createDevice() {
        return DeviceEntity.builder()
                           .name("name")
                           .deviceId("deviceId")
                           .applicationUserId(STUDENT_ID)
                           .build();
    }


}
