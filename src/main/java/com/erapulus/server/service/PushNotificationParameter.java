package com.erapulus.server.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushNotificationParameter {
    SOUND("default"),
    COLOR("#FFFF00"),
    TOPIC("New friend request");

    private final String value;

}

