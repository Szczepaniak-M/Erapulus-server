package com.erapulus.server.mapper;

import com.erapulus.server.database.model.DeviceEntity;
import com.erapulus.server.dto.device.DeviceResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DeviceEntityToResponseDtoMapper implements EntityToResponseDtoMapper<DeviceEntity, DeviceResponseDto> {
    @Override
    public DeviceResponseDto from(DeviceEntity deviceEntity) {
        return DeviceResponseDto.builder()
                                .id(deviceEntity.id())
                                .deviceId(deviceEntity.deviceId())
                                .name(deviceEntity.name())
                                .applicationUserId(deviceEntity.applicationUserId())

                                .build();
    }
}
