package com.erapulus.server.service;

import com.erapulus.server.database.model.ApplicationUserEntity;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Service
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;
    private final AndroidConfig androidConfig;
    private final ApnsConfig apnsConfig;

    public FCMService() {
        FirebaseMessaging firebaseMessaging = null;
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                                                     .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("/firebase.json").getInputStream()))
                                                     .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            firebaseMessaging = FirebaseMessaging.getInstance();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        this.androidConfig = createAndroidConfig();
        this.apnsConfig = createApnsConfig();
        this.firebaseMessaging = firebaseMessaging;
    }

    public Mono<Void> sendPushNotification(String deviceId, ApplicationUserEntity user) {
        Message message = getMessageWithData(deviceId, user);
        return Mono.fromCallable(() -> firebaseMessaging.sendAsync(message))
                   .doOnError(e -> log.error(e.getMessage(), e))
                   .onErrorResume(e -> Mono.empty())
                   .then();
    }

    private AndroidConfig createAndroidConfig() {
        return AndroidConfig.builder()
                            .setTtl(Duration.ofMinutes(2).toMillis())
                            .setCollapseKey(PushNotificationParameter.TOPIC.value())
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                                                .setSound(PushNotificationParameter.SOUND.value())
                                                                .setColor(PushNotificationParameter.COLOR.value())
                                                                .setTag(PushNotificationParameter.TOPIC.value())
                                                                .build())
                            .build();
    }

    private ApnsConfig createApnsConfig() {
        return ApnsConfig.builder()
                         .setAps(Aps.builder()
                                    .setCategory(PushNotificationParameter.TOPIC.value())
                                    .setThreadId(PushNotificationParameter.TOPIC.value())
                                    .build())
                         .build();
    }

    private Message getMessageWithData(String deviceId, ApplicationUserEntity user) {
        return Message.builder()
                      .setApnsConfig(apnsConfig)
                      .setAndroidConfig(androidConfig)
                      .setNotification(Notification.builder()
                                                   .setTitle(PushNotificationParameter.TOPIC.value())
                                                   .setBody("%s %s wants to be your new friend".formatted(user.firstName(), user.lastName()))
                                                   .build())
                      .setToken(deviceId)
                      .build();
    }
}
