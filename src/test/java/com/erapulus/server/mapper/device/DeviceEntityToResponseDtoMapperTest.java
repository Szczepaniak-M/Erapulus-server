package com.erapulus.server.mapper.device;

import com.erapulus.server.database.model.DeviceEntity;
import com.erapulus.server.dto.device.DeviceResponseDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeviceEntityToResponseDtoMapperTest {

    private static final int ID = 1;
    private static final String NAME = "name";
    private static final String DEVICE_ID = "deviceId";
    private static final Integer USER_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        DeviceEntity entity = DeviceEntity.builder()
                                          .id(ID)
                                          .name(NAME)
                                          .deviceId(DEVICE_ID)
                                          .applicationUserId(USER_ID)
                                          .build();

        // when
        DeviceResponseDto result = new DeviceEntityToResponseDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(DEVICE_ID, result.deviceId());
        assertEquals(USER_ID, result.applicationUserId());
        assertTrue(ValidatorUtils.createValidator().validate(result).isEmpty());
    }
}