package com.erapulus.server.device.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.device.database.DeviceEntity;
import com.erapulus.server.device.dto.DeviceResponseDto;
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
