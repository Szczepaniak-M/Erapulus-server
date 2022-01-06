package com.erapulus.server.device.mapper;

import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.device.database.DeviceEntity;
import com.erapulus.server.device.dto.DeviceRequestDto;
import org.springframework.stereotype.Component;

@Component
public class DeviceRequestDtoToEntityMapper implements RequestDtoToEntityMapper<DeviceRequestDto, DeviceEntity> {
    @Override
    public DeviceEntity from(DeviceRequestDto deviceRequestDto) {
        return DeviceEntity.builder()
                           .deviceId(deviceRequestDto.deviceId())
                           .name(deviceRequestDto.name())
                           .build();
    }
}
