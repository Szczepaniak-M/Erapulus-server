package com.erapulus.server.mapper.device;

import com.erapulus.server.database.model.DeviceEntity;
import com.erapulus.server.dto.device.DeviceRequestDto;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class DeviceRequestDtoToEntityMapper implements RequestDtoToEntityMapper<DeviceRequestDto, DeviceEntity> {
    @Override
    public DeviceEntity from(DeviceRequestDto deviceRequestDto) {
        return DeviceEntity.builder()
                           .deviceId(deviceRequestDto.deviceId())
                           .name(deviceRequestDto.name())
                           .applicationUserId(deviceRequestDto.applicationUserId())
                           .build();
    }
}
