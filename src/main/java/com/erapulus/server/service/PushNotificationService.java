package com.erapulus.server.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PushNotificationService {

    Mono<Void> sendPushNotification() {
        return Mono.empty();
    }
}
