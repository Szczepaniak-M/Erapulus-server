package com.erapulus.server.mapper.device;

import com.erapulus.server.database.model.DeviceEntity;
import com.erapulus.server.dto.device.DeviceRequestDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
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
        assertThat(ValidatorUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "applicationUserId"));
    }
}