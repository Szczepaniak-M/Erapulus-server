package com.erapulus.server.mapper.building;

import com.erapulus.server.database.model.BuildingEntity;
import com.erapulus.server.dto.building.BuildingResponseDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildingEntityToResponseDtoMapperTest {

    private static final int ID = 1;
    private static final String NAME = "name";
    private static final String ABBREV = "abbrev";
    private static final double LONGITUDE = 1.5;
    private static final double LATITUDE = 2.5;
    private static final Integer UNIVERSITY_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        BuildingEntity entity = BuildingEntity.builder()
                                              .id(ID)
                                              .name(NAME)
                                              .abbrev(ABBREV)
                                              .longitude(LONGITUDE)
                                              .latitude(LATITUDE)
                                              .universityId(UNIVERSITY_ID)
                                              .build();

        // when
        BuildingResponseDto result = new BuildingEntityToResponseDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(ABBREV, result.abbrev());
        assertEquals(LONGITUDE, result.longitude());
        assertEquals(LATITUDE, result.latitude());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertTrue(ValidatorUtils.createValidator().validate(result).isEmpty());
    }
}