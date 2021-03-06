package com.erapulus.server.friendship.service;

import com.erapulus.server.applicationuser.database.ApplicationUserRepository;
import com.erapulus.server.device.database.DeviceRepository;
import com.erapulus.server.friendship.dto.FriendshipResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
class PushNotificationService {

    private final DeviceRepository deviceRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final FCMService fcmService;

    Mono<Void> sendPushNotification(FriendshipResponseDto friendshipResponseDto) {
        return deviceRepository.findAllByStudentId(friendshipResponseDto.applicationUserId())
                               .zipWith(applicationUserRepository.findById(friendshipResponseDto.friendId()).cache().repeat())
                               .flatMap(deviceAndUser -> fcmService.sendPushNotification(deviceAndUser.getT1().deviceId(), deviceAndUser.getT2()))
                               .then();
    }
}
