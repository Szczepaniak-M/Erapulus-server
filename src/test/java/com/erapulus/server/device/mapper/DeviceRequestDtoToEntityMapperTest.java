package com.erapulus.server.device.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.device.database.DeviceEntity;
import com.erapulus.server.device.dto.DeviceRequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DeviceRequestDtoToEntityMapperTest {

    private static final String NAME = "name";
    private static final String DEVICE_ID = "deviceId";

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        DeviceRequestDto requestDto = DeviceRequestDto.builder()
                                                      .name(NAME)
                                                      .deviceId(DEVICE_ID)
                                                      .build();

        // when
        DeviceEntity result = new DeviceRequestDtoToEntityMapper().from(requestDto);

        //then
        assertNull(result.id());
        assertEquals(NAME, result.name());
        assertEquals(DEVICE_ID, result.deviceId());
        assertNull(result.applicationUserId());
        assertThat(TestUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "applicationUserId"));
    }
}