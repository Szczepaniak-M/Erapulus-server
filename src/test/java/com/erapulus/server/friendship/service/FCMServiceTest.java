package com.erapulus.server.friendship.service;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.google.api.core.SettableApiFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FCMServiceTest {

    private static final String DEVICE_ID = "deviceId";

    @Mock
    private FirebaseMessaging firebaseMessaging;

    private FCMService fcmService;


    @BeforeEach
    void setUp() {
        try (MockedStatic<FirebaseMessaging> utilities = mockStatic(FirebaseMessaging.class)) {
            utilities.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            this.fcmService = new FCMService();
        }
    }

    @Test
    void sendPushNotification_shouldReturnEmptyOnSuccess() {
        // given
        var user = createUser();
        when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(SettableApiFuture.create());

        // when
        Mono<Void> result = fcmService.sendPushNotification(DEVICE_ID, user);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void sendPushNotification_shouldReturnEmptyOnError() {
        // given
        var user = createUser();
        when(firebaseMessaging.sendAsync(any(Message.class))).thenThrow(new RuntimeException());

        // when
        Mono<Void> result = fcmService.sendPushNotification(DEVICE_ID, user);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    private ApplicationUserEntity createUser() {
        return ApplicationUserEntity.builder()
                                    .id(1)
                                    .firstName("name")
                                    .lastName("deviceId")
                                    .build();
    }
}
