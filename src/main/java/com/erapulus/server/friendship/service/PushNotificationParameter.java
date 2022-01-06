package com.erapulus.server.friendship.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum PushNotificationParameter {
    SOUND("default"),
    COLOR("#FFFF00"),
    TOPIC("New friend request");

    private final String value;

}

